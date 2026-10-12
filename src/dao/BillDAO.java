package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillDAO.class);
    private static final BigDecimal MAX_CURRENCY_AMOUNT = new BigDecimal("9999999999.99");

    // =========================================================
    // ADD BILL
    // =========================================================

    public boolean addBill(
            int patientId,
            int admissionId,
            BigDecimal roomCharges,
            BigDecimal doctorCharges,
            BigDecimal medicineCharges,
            BigDecimal otherCharges,
            BigDecimal paidAmount
    ) {
        return addBill(patientId, Integer.valueOf(admissionId), roomCharges, doctorCharges,
                medicineCharges, otherCharges, paidAmount);
    }

    public boolean addBill(
            int patientId,
            Integer admissionId,
            BigDecimal roomCharges,
            BigDecimal doctorCharges,
            BigDecimal medicineCharges,
            BigDecimal otherCharges,
            BigDecimal paidAmount
    ) {
        BigDecimal totalAmount = calculateTotal(roomCharges, doctorCharges, medicineCharges, otherCharges);

        if (!isValidPaidAmount(paidAmount, totalAmount)) {
            throw new IllegalArgumentException(
                    "Paid amount must be a valid currency amount between zero and the bill total.");
        }

        String paymentStatus =
                getPaymentStatus(totalAmount, paidAmount);

        String sql =
                "INSERT INTO bills " +
                "(patient_id, admission_id, room_charges, " +
                "doctor_charges, medicine_charges, other_charges, " +
                "total_amount, paid_amount, payment_status) " +
                "SELECT ?, ?, ?, ?, ?, ?, ?, ?, ? " +
                "WHERE EXISTS (SELECT 1 FROM patients WHERE patient_id = ?) " +
                "AND (CAST(? AS INTEGER) IS NULL OR EXISTS " +
                "(SELECT 1 FROM admissions WHERE admission_id = ? AND patient_id = ?))";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);
            setNullableInteger(statement, 2, admissionId);
            statement.setBigDecimal(3, roomCharges);
            statement.setBigDecimal(4, doctorCharges);
            statement.setBigDecimal(5, medicineCharges);
            statement.setBigDecimal(6, otherCharges);
            statement.setBigDecimal(7, totalAmount);
            statement.setBigDecimal(8, paidAmount);
            statement.setString(9, paymentStatus);
            statement.setInt(10, patientId);
            setNullableInteger(statement, 11, admissionId);
            setNullableInteger(statement, 12, admissionId);
            statement.setInt(13, patientId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Unable to save bill.", e);
            throw new IllegalStateException("Unable to save bill.", e);
        }
    }

    // =========================================================
    // UPDATE BILL
    // =========================================================

    public boolean updateBill(
            int billId,
            int patientId,
            int admissionId,
            BigDecimal roomCharges,
            BigDecimal doctorCharges,
            BigDecimal medicineCharges,
            BigDecimal otherCharges,
            BigDecimal paidAmount
    ) {
        return updateBill(billId, patientId, Integer.valueOf(admissionId), roomCharges,
                doctorCharges, medicineCharges, otherCharges, paidAmount);
    }

    public boolean updateBill(
            int billId,
            int patientId,
            Integer admissionId,
            BigDecimal roomCharges,
            BigDecimal doctorCharges,
            BigDecimal medicineCharges,
            BigDecimal otherCharges,
            BigDecimal paidAmount
    ) {
        BigDecimal totalAmount = calculateTotal(roomCharges, doctorCharges, medicineCharges, otherCharges);

        if (!isValidPaidAmount(paidAmount, totalAmount)) {
            throw new IllegalArgumentException("Paid amount must be non-negative and no greater than the bill total.");
        }

        String paymentStatus =
                getPaymentStatus(totalAmount, paidAmount);

        String sql =
                "UPDATE bills SET " +
                "patient_id = ?, " +
                "admission_id = ?, " +
                "room_charges = ?, " +
                "doctor_charges = ?, " +
                "medicine_charges = ?, " +
                "other_charges = ?, " +
                "total_amount = ?, " +
                "paid_amount = ?, " +
                "payment_status = ? " +
                "WHERE bill_id = ? " +
                "AND EXISTS (SELECT 1 FROM patients WHERE patient_id = ?) " +
                "AND (CAST(? AS INTEGER) IS NULL OR EXISTS " +
                "(SELECT 1 FROM admissions WHERE admission_id = ? AND patient_id = ?))";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                BigDecimal existingPaid;
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT paid_amount FROM bills WHERE bill_id = ? FOR UPDATE")) {
                    lock.setInt(1, billId);
                    try (ResultSet result = lock.executeQuery()) {
                        if (!result.next()) {
                            connection.rollback();
                            return false;
                        }
                        existingPaid = result.getBigDecimal("paid_amount");
                    }
                }
                if (paidAmount.compareTo(existingPaid) != 0) {
                    throw new IllegalArgumentException("Paid amount can only be changed by recording a payment.");
                }

                int updated;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, patientId);
                    setNullableInteger(statement, 2, admissionId);
                    statement.setBigDecimal(3, roomCharges);
                    statement.setBigDecimal(4, doctorCharges);
                    statement.setBigDecimal(5, medicineCharges);
                    statement.setBigDecimal(6, otherCharges);
                    statement.setBigDecimal(7, totalAmount);
                    statement.setBigDecimal(8, paidAmount);
                    statement.setString(9, paymentStatus);
                    statement.setInt(10, billId);
                    statement.setInt(11, patientId);
                    setNullableInteger(statement, 12, admissionId);
                    setNullableInteger(statement, 13, admissionId);
                    statement.setInt(14, patientId);
                    updated = statement.executeUpdate();
                }
                if (updated == 0) {
                    connection.rollback();
                    return false;
                }
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to update bill {}.", billId, e);
            throw new IllegalStateException("Unable to update bill.", e);
        }
    }

    // =========================================================
    // DELETE BILL
    // =========================================================

    public boolean deleteBill(int billId) {

        String sql =
                "DELETE FROM bills WHERE bill_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, billId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.error("Unable to delete bill {}.", billId, e);
            throw new IllegalStateException("Unable to delete bill.", e);
        }
    }

    // =========================================================
    // GET ALL BILLS
    // =========================================================

    public List<Object[]> getAllBills() {

        List<Object[]> bills = new ArrayList<>();

        String sql =
                "SELECT " +
                "bill_id, " +
                "patient_id, " +
                "admission_id, " +
                "room_charges, " +
                "doctor_charges, " +
                "medicine_charges, " +
                "other_charges, " +
                "total_amount, " +
                "paid_amount, " +
                "payment_status, " +
                "bill_date " +
                "FROM bills " +
                "ORDER BY bill_id DESC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Object[] row = {
                        resultSet.getInt("bill_id"),
                        resultSet.getInt("patient_id"),
                        resultSet.getObject("admission_id", Integer.class),
                        resultSet.getBigDecimal("room_charges"),
                        resultSet.getBigDecimal("doctor_charges"),
                        resultSet.getBigDecimal("medicine_charges"),
                        resultSet.getBigDecimal("other_charges"),
                        resultSet.getBigDecimal("total_amount"),
                        resultSet.getBigDecimal("paid_amount"),
                        resultSet.getString("payment_status"),
                        resultSet.getTimestamp("bill_date")
                };

                bills.add(row);
            }

        } catch (Exception e) {
            LOGGER.error("Unable to load bills.", e);
            throw new IllegalStateException("Unable to load bills.", e);
        }

        return bills;
    }

    // =========================================================
    // GET BILLS BY PATIENT
    // =========================================================

    public List<Object[]> getBillsByPatient(int patientId) {

        List<Object[]> bills = new ArrayList<>();

        String sql =
                "SELECT " +
                "bill_id, " +
                "patient_id, " +
                "admission_id, " +
                "room_charges, " +
                "doctor_charges, " +
                "medicine_charges, " +
                "other_charges, " +
                "total_amount, " +
                "paid_amount, " +
                "payment_status, " +
                "bill_date " +
                "FROM bills " +
                "WHERE patient_id = ? " +
                "ORDER BY bill_id DESC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Object[] row = {
                            resultSet.getInt("bill_id"),
                            resultSet.getInt("patient_id"),
                            resultSet.getObject("admission_id", Integer.class),
                            resultSet.getBigDecimal("room_charges"),
                            resultSet.getBigDecimal("doctor_charges"),
                            resultSet.getBigDecimal("medicine_charges"),
                            resultSet.getBigDecimal("other_charges"),
                            resultSet.getBigDecimal("total_amount"),
                            resultSet.getBigDecimal("paid_amount"),
                            resultSet.getString("payment_status"),
                            resultSet.getTimestamp("bill_date")
                    };

                    bills.add(row);
                }
            }

        } catch (Exception e) {
            LOGGER.error("Unable to load bills for patient {}.", patientId, e);
            throw new IllegalStateException("Unable to load patient bills.", e);
        }

        return bills;
    }

    // =========================================================
    // GET SINGLE BILL
    // =========================================================

    public Object[] getBillById(int billId) {

        String sql =
                "SELECT " +
                "bill_id, " +
                "patient_id, " +
                "admission_id, " +
                "room_charges, " +
                "doctor_charges, " +
                "medicine_charges, " +
                "other_charges, " +
                "total_amount, " +
                "paid_amount, " +
                "payment_status, " +
                "bill_date " +
                "FROM bills " +
                "WHERE bill_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, billId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Object[]{
                            resultSet.getInt("bill_id"),
                            resultSet.getInt("patient_id"),
                            resultSet.getObject("admission_id", Integer.class),
                            resultSet.getBigDecimal("room_charges"),
                            resultSet.getBigDecimal("doctor_charges"),
                            resultSet.getBigDecimal("medicine_charges"),
                            resultSet.getBigDecimal("other_charges"),
                            resultSet.getBigDecimal("total_amount"),
                            resultSet.getBigDecimal("paid_amount"),
                            resultSet.getString("payment_status"),
                            resultSet.getTimestamp("bill_date")
                    };
                }
            }

        } catch (Exception e) {
            LOGGER.error("Unable to load bill {}.", billId, e);
            throw new IllegalStateException("Unable to load bill details.", e);
        }

        return null;
    }

    // =========================================================
    // ADD PAYMENT
    // =========================================================

    public boolean addPayment(
            int billId,
            BigDecimal paymentAmount
    ) {

        if (!isValidPaymentAmount(paymentAmount)) {
            return false;
        }

        String balanceSql = "SELECT total_amount, paid_amount FROM bills WHERE bill_id = ?";
        String updateSql =
                "UPDATE bills SET paid_amount = paid_amount + ?, "
                + "payment_status = CASE WHEN paid_amount + ? >= total_amount "
                + "THEN 'PAID' ELSE 'PARTIAL' END "
                + "WHERE bill_id = ? AND paid_amount + ? <= total_amount";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement balanceStatement = connection.prepareStatement(
                        balanceSql + " FOR UPDATE")) {
                    balanceStatement.setInt(1, billId);
                    try (ResultSet balance = balanceStatement.executeQuery()) {
                        if (!balance.next() || !isPaymentWithinBalance(paymentAmount,
                                balance.getBigDecimal("total_amount"), balance.getBigDecimal("paid_amount"))) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setBigDecimal(1, paymentAmount);
                    updateStatement.setBigDecimal(2, paymentAmount);
                    updateStatement.setInt(3, billId);
                    updateStatement.setBigDecimal(4, paymentAmount);
                    if (updateStatement.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to record payment for bill {}.", billId, e);
            throw new IllegalStateException("Unable to record bill payment.", e);
        }
    }

    // =========================================================
    // GET REMAINING AMOUNT
    // =========================================================

    public BigDecimal getRemainingAmount(int billId) {

        String sql =
                "SELECT total_amount, paid_amount " +
                "FROM bills WHERE bill_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, billId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    BigDecimal total =
                            resultSet.getBigDecimal("total_amount");

                    BigDecimal paid =
                            resultSet.getBigDecimal("paid_amount");

                    return total.subtract(paid);
                }

            }

        } catch (Exception e) {
            LOGGER.error("Unable to load remaining balance for bill {}.", billId, e);
            throw new IllegalStateException("Unable to load remaining bill balance.", e);
        }

        throw new IllegalArgumentException("Bill was not found.");
    }

    private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) statement.setNull(index, java.sql.Types.INTEGER);
        else statement.setInt(index, value);
    }

    private void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }

    // =========================================================
    // PAYMENT STATUS
    // =========================================================

    static String getPaymentStatus(
            BigDecimal totalAmount,
            BigDecimal paidAmount
    ) {

        if (paidAmount.signum() <= 0) {
            return "PENDING";

        } else if (paidAmount.compareTo(totalAmount) >= 0) {
            return "PAID";

        } else {
            return "PARTIAL";
        }
    }

    // =========================================================
    // TOTAL BILL
    // =========================================================

    public BigDecimal calculateTotal(
            BigDecimal roomCharges,
            BigDecimal doctorCharges,
            BigDecimal medicineCharges,
            BigDecimal otherCharges
    ) {
        if (!isCurrencyAmount(roomCharges) || !isCurrencyAmount(doctorCharges)
                || !isCurrencyAmount(medicineCharges) || !isCurrencyAmount(otherCharges)) {
            throw new IllegalArgumentException("Charges must be non-negative currency amounts with at most two decimals.");
        }
        BigDecimal total = roomCharges.add(doctorCharges).add(medicineCharges).add(otherCharges);
        if (total.compareTo(MAX_CURRENCY_AMOUNT) > 0) {
            throw new IllegalArgumentException("Total charges exceed the supported billing amount.");
        }
        return total.setScale(2, RoundingMode.UNNECESSARY);
    }

    static boolean isCurrencyAmount(BigDecimal amount) {
        if (amount == null || amount.signum() < 0 || amount.compareTo(MAX_CURRENCY_AMOUNT) > 0) {
            return false;
        }
        try {
            amount.setScale(2, RoundingMode.UNNECESSARY);
            return true;
        } catch (ArithmeticException exception) {
            return false;
        }
    }

    static boolean isValidPaidAmount(BigDecimal paidAmount, BigDecimal totalAmount) {
        return isCurrencyAmount(paidAmount) && isCurrencyAmount(totalAmount)
                && paidAmount.compareTo(totalAmount) <= 0;
    }

    static boolean isValidPaymentAmount(BigDecimal paymentAmount) {
        return isCurrencyAmount(paymentAmount) && paymentAmount.signum() > 0;
    }

    static boolean isPaymentWithinBalance(BigDecimal paymentAmount, BigDecimal totalAmount,
                                          BigDecimal paidAmount) {
        return isValidPaymentAmount(paymentAmount) && isValidPaidAmount(paidAmount, totalAmount)
                && paymentAmount.compareTo(totalAmount.subtract(paidAmount)) <= 0;
    }
}
