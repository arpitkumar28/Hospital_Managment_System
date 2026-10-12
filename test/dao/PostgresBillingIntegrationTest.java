package dao;

import database.DatabaseConnection;
import model.BillingReport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "HOSPITAL_TEST_DB_NAME", matches = "[A-Za-z0-9_]+_test")
class PostgresBillingIntegrationTest {
    private static final BillDAO bills = new BillDAO();
    private static final ReportDAO reports = new ReportDAO();
    private static int patientId;
    private static int roomId;
    private static int bedId;
    private static int admissionId;
    private static LocalDate today;
    private static BillingReport baseline;

    @BeforeAll
    static void setUp() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String database;
            try (PreparedStatement statement = connection.prepareStatement("SELECT current_database()");
                 ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                database = result.getString(1);
            }
            assertEquals(System.getenv("HOSPITAL_TEST_DB_NAME"), database,
                    "Integration tests must connect to the exact dedicated PostgreSQL test database.");

            today = LocalDate.now();
            baseline = reports.getBillingReport(today.withDayOfMonth(1), today);
            try (PreparedStatement patient = connection.prepareStatement(
                    "INSERT INTO patients (name) VALUES (?) RETURNING patient_id")) {
                patient.setString(1, "Billing integration " + java.util.UUID.randomUUID());
                try (ResultSet result = patient.executeQuery()) {
                    assertTrue(result.next());
                    patientId = result.getInt(1);
                }
            }
            try (PreparedStatement room = connection.prepareStatement(
                    "INSERT INTO rooms (room_number, room_type, price_per_day) VALUES (?, ?, 0) RETURNING room_id")) {
                room.setString(1, "IT-" + patientId);
                room.setString(2, "Test");
                try (ResultSet result = room.executeQuery()) {
                    assertTrue(result.next());
                    roomId = result.getInt(1);
                }
            }
            try (PreparedStatement bed = connection.prepareStatement(
                    "INSERT INTO beds (room_id, bed_number) VALUES (?, ?) RETURNING bed_id")) {
                bed.setInt(1, roomId);
                bed.setString(2, "IT-" + patientId);
                try (ResultSet result = bed.executeQuery()) {
                    assertTrue(result.next());
                    bedId = result.getInt(1);
                }
            }
            try (PreparedStatement admission = connection.prepareStatement(
                    "INSERT INTO admissions (patient_id, bed_id, admission_date) VALUES (?, ?, ?) RETURNING admission_id")) {
                admission.setInt(1, patientId);
                admission.setInt(2, bedId);
                admission.setDate(3, java.sql.Date.valueOf(today));
                try (ResultSet result = admission.executeQuery()) {
                    assertTrue(result.next());
                    admissionId = result.getInt(1);
                }
            }
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (patientId == 0) return;
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM bills WHERE patient_id = ?")) {
                statement.setInt(1, patientId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM admissions WHERE admission_id = ?")) {
                statement.setInt(1, admissionId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM beds WHERE bed_id = ?")) {
                statement.setInt(1, bedId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE room_id = ?")) {
                statement.setInt(1, roomId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM patients WHERE patient_id = ?")) {
                statement.setInt(1, patientId);
                statement.executeUpdate();
            }
        }
    }

    @Test
    void billingReferencesPaymentsAndReportsReconcileAgainstPostgres() throws Exception {
        BigDecimal total = new BigDecimal("100.00");
        assertFalse(bills.addBill(Integer.MAX_VALUE, admissionId, total, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertFalse(bills.addBill(patientId, Integer.MAX_VALUE, total, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertTrue(bills.addBill(patientId, admissionId, total, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertTrue(bills.addBill(patientId, (Integer) null, new BigDecimal("20.00"), BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        Object[] optionalAdmissionBill = bills.getBillsByPatient(patientId).stream()
                .filter(row -> row[2] == null).findFirst().orElseThrow();
        int optionalBillId = (int) optionalAdmissionBill[0];
        assertTrue(bills.updateBill(optionalBillId, patientId, null, new BigDecimal("20.00"),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertTrue(bills.deleteBill(optionalBillId));

        Object[] admittedBill = bills.getBillsByPatient(patientId).stream()
                .filter(row -> Integer.valueOf(admissionId).equals(row[2])).findFirst().orElseThrow();
        int billId = (int) admittedBill[0];
        assertEquals(admissionId, admittedBill[2]);
        assertTrue(bills.addPayment(billId, new BigDecimal("25.00")));
        assertFalse(bills.addPayment(billId, new BigDecimal("75.01")));
        CountDownLatch start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> first = executor.submit(() -> {
                start.await();
                return bills.addPayment(billId, new BigDecimal("40.00"));
            });
            Future<Boolean> second = executor.submit(() -> {
                start.await();
                return bills.addPayment(billId, new BigDecimal("40.00"));
            });
            start.countDown();
            assertNotEquals(first.get(), second.get(),
                    "Only one concurrent payment may fit in the remaining balance.");
        } finally {
            executor.shutdownNow();
        }
        assertThrows(IllegalArgumentException.class,
                () -> bills.updateBill(billId, patientId, admissionId, total, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> bills.updateBill(billId, patientId, admissionId, total, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("66.00")));
        assertEquals(new BigDecimal("65.00"), bills.getBillById(billId)[8]);

        BillingReport actual = reports.getBillingReport(today.withDayOfMonth(1), today);
        assertEquals(baseline.billCount() + 1, actual.billCount());
        assertEquals(baseline.billedAmount().add(new BigDecimal("100.00")), actual.billedAmount());
        assertEquals(baseline.collectedAmount().add(new BigDecimal("65.00")), actual.collectedAmount());
        assertEquals(baseline.outstandingAmount().add(new BigDecimal("35.00")), actual.outstandingAmount());
        assertEquals(actual.billCount(), actual.paymentStatusTotals().stream()
                .mapToLong(BillingReport.PaymentStatusTotal::billCount).sum());
        assertEquals(actual.billedAmount(), actual.paymentStatusTotals().stream()
                .map(BillingReport.PaymentStatusTotal::billedAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        assertTrue(actual.dailyRevenue().stream().anyMatch(row -> row.period().equals(today)
                && row.collectedAmount().compareTo(new BigDecimal("25.00")) >= 0));
        assertTrue(actual.monthlyRevenue().stream().anyMatch(row -> row.period().equals(today.withDayOfMonth(1))
                && row.collectedAmount().compareTo(new BigDecimal("25.00")) >= 0));
    }
}
