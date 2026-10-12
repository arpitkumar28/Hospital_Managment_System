package dao;

import database.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "HOSPITAL_TEST_DB_NAME", matches = "[A-Za-z0-9_]+_test")
class PostgresAdmissionBedIntegrationTest {
    private static final AdmissionDAO admissions = new AdmissionDAO();
    private static final BedDAO beds = new BedDAO();
    private static int patientId;
    private static int secondPatientId;
    private static int roomId;
    private static int availableBedId;
    private static int occupiedBedId;
    private static int activeAdmissionId;
    private static final String marker = "Admission test " + UUID.randomUUID();

    @BeforeAll
    static void setUp() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String database;
            try (PreparedStatement statement = connection.prepareStatement("SELECT current_database()" );
                 ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                database = result.getString(1);
            }
            assertEquals(System.getenv("HOSPITAL_TEST_DB_NAME"), database,
                    "Integration tests must connect to the exact dedicated PostgreSQL test database.");

            patientId = insertPatient(connection, marker + " patient A");
            secondPatientId = insertPatient(connection, marker + " patient B");
            roomId = insertRoom(connection);
            availableBedId = insertBed(connection, "available");
            occupiedBedId = insertBed(connection, "occupied");
            activeAdmissionId = insertAdmission(connection, patientId, occupiedBedId,
                    Date.valueOf(LocalDate.now()));
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE beds SET status = 'OCCUPIED' WHERE bed_id = ?")) {
                statement.setInt(1, occupiedBedId);
                assertEquals(1, statement.executeUpdate());
            }
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (patientId == 0) return;
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM admissions WHERE admission_id = ? OR patient_id IN (?, ?)")) {
                statement.setInt(1, activeAdmissionId);
                statement.setInt(2, patientId);
                statement.setInt(3, secondPatientId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM beds WHERE bed_id IN (?, ?)")) {
                statement.setInt(1, availableBedId);
                statement.setInt(2, occupiedBedId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE room_id = ?")) {
                statement.setInt(1, roomId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM patients WHERE patient_id IN (?, ?)")) {
                statement.setInt(1, patientId);
                statement.setInt(2, secondPatientId);
                statement.executeUpdate();
            }
        }
    }

    @BeforeEach
    void resetBedStates() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE beds SET status = CASE WHEN bed_id = ? THEN 'OCCUPIED' ELSE 'AVAILABLE' END " +
                             "WHERE bed_id IN (?, ?)")) {
            statement.setInt(1, occupiedBedId);
            statement.setInt(2, occupiedBedId);
            statement.setInt(3, availableBedId);
            statement.executeUpdate();
        }
    }

    @Test
    void admissionRejectsMissingOccupiedAndAlreadyAssignedBeds() throws Exception {
        Date today = Date.valueOf(LocalDate.now());
        assertFalse(admissions.admitPatient(patientId, Integer.MAX_VALUE, today));
        assertFalse(admissions.admitPatient(secondPatientId, occupiedBedId, today));

        // A stale AVAILABLE status must not permit a second active admission.
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE beds SET status = 'AVAILABLE' WHERE bed_id = ?")) {
            statement.setInt(1, occupiedBedId);
            assertEquals(1, statement.executeUpdate());
        }
        assertFalse(admissions.admitPatient(secondPatientId, occupiedBedId, today));
        assertEquals(1, activeAdmissionCount(occupiedBedId));
    }

    @Test
    void admissionRejectsNullDateAndAvailableBedLookupReflectsCurrentState() throws Exception {
        assertThrows(IllegalArgumentException.class,
                () -> admissions.admitPatient(patientId, availableBedId, null));
        assertTrue(beds.getAvailableBeds().stream().anyMatch(row ->
                ((Number) row[0]).intValue() == availableBedId));

        assertTrue(admissions.admitPatient(patientId, availableBedId, Date.valueOf(LocalDate.now())));
        assertFalse(beds.getAvailableBeds().stream().anyMatch(row ->
                ((Number) row[0]).intValue() == availableBedId));
    }

    @Test
    void dischargeDateValidationAndFailedBedUpdateLeaveBothRecordsUnchanged() throws Exception {
        Date admissionDate = admissionDate(activeAdmissionId);
        Date earlierDate = Date.valueOf(admissionDate.toLocalDate().minusDays(1));
        assertFalse(admissions.dischargePatient(activeAdmissionId, earlierDate));
        assertThrows(IllegalArgumentException.class,
                () -> admissions.dischargePatient(activeAdmissionId, null));
        assertNull(dischargeDate(activeAdmissionId));
        assertEquals("OCCUPIED", bedStatus(occupiedBedId));

        assertNull(dischargeDate(activeAdmissionId));
        assertEquals("OCCUPIED", bedStatus(occupiedBedId));
    }

    @Test
    void dischargeRollsBackAdmissionWhenBedUpdateFails() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "CREATE OR REPLACE FUNCTION test_fail_admission_bed_update() RETURNS trigger AS $$ " +
                             "BEGIN IF OLD.bed_id = " + occupiedBedId + " THEN " +
                             "RAISE EXCEPTION 'forced bed update failure'; END IF; RETURN NEW; END; " +
                             "$$ LANGUAGE plpgsql")) {
            statement.execute();
        }
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "CREATE TRIGGER test_fail_admission_bed_update BEFORE UPDATE ON beds " +
                             "FOR EACH ROW EXECUTE FUNCTION test_fail_admission_bed_update()")) {
            statement.execute();
        }

        try {
            assertThrows(IllegalStateException.class,
                    () -> admissions.dischargePatient(activeAdmissionId, Date.valueOf(LocalDate.now())));
            assertNull(dischargeDate(activeAdmissionId), "Admission update must roll back with failed bed update.");
            assertEquals("OCCUPIED", bedStatus(occupiedBedId));
        } finally {
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "DROP TRIGGER IF EXISTS test_fail_admission_bed_update ON beds")) {
                statement.execute();
            }
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "DROP FUNCTION IF EXISTS test_fail_admission_bed_update()")) {
                statement.execute();
            }
        }
    }

    @Test
    void invalidRoomReferencesAreRejectedByDatabaseConstraints() {
        assertFalse(beds.addBed(Integer.MAX_VALUE, "invalid-room-" + UUID.randomUUID()));
        assertFalse(beds.updateBed(availableBedId, Integer.MAX_VALUE, "invalid-room-update", "AVAILABLE"));
    }

    private static int insertPatient(Connection connection, String name) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO patients (name) VALUES (?) RETURNING patient_id")) {
            statement.setString(1, name);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int insertRoom(Connection connection) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO rooms (room_number, room_type, price_per_day) VALUES (?, 'Test', 0) RETURNING room_id")) {
            statement.setString(1, "IT-" + UUID.randomUUID());
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int insertBed(Connection connection, String suffix) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO beds (room_id, bed_number) VALUES (?, ?) RETURNING bed_id")) {
            statement.setInt(1, roomId);
            statement.setString(2, suffix + "-" + UUID.randomUUID());
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int insertAdmission(Connection connection, int patient, int bed, Date date) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO admissions (patient_id, bed_id, admission_date) VALUES (?, ?, ?) RETURNING admission_id")) {
            statement.setInt(1, patient);
            statement.setInt(2, bed);
            statement.setDate(3, date);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int activeAdmissionCount(int bed) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT count(*) FROM admissions WHERE bed_id = ? AND discharge_date IS NULL")) {
            statement.setInt(1, bed);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static Date admissionDate(int admission) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT admission_date FROM admissions WHERE admission_id = ?")) {
            statement.setInt(1, admission);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getDate(1); }
        }
    }

    private static Date dischargeDate(int admission) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT discharge_date FROM admissions WHERE admission_id = ?")) {
            statement.setInt(1, admission);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getDate(1); }
        }
    }

    private static String bedStatus(int bed) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT status FROM beds WHERE bed_id = ?")) {
            statement.setInt(1, bed);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getString(1); }
        }
    }
}
