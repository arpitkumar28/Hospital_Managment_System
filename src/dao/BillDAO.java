package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BillDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillDAO.class);

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

        BigDecimal totalAmount = calculateTotal(roomCharges, doctorCharges, medicineCharges, otherCharges);

        if (paidAmount == null || paidAmount.signum() < 0 || paidAmount.compareTo(totalAmount) > 0) {
            return false;
        }

        String paymentStatus =
                getPaymentStatus(totalAmount, paidAmount);

        String sql =
                "INSERT INTO bills " +
                "(patient_id, admission_id, room_charges, " +
                "doctor_charges, medicine_charges, other_charges, " +
                "total_amount, paid_amount, payment_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);
            statement.setInt(2, admissionId);
            statement.setBigDecimal(3, roomCharges);
            statement.setBigDecimal(4, doctorCharges);
            statement.setBigDecimal(5, medicineCharges);
            statement.setBigDecimal(6, otherCharges);
            statement.setBigDecimal(7, totalAmount);
            statement.setBigDecimal(8, paidAmount);
            statement.setString(9, paymentStatus);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
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

        BigDecimal totalAmount = calculateTotal(roomCharges, doctorCharges, medicineCharges, otherCharges);

        if (paidAmount == null || paidAmount.signum() < 0 || paidAmount.compareTo(totalAmount) > 0) {
            return false;
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
                "WHERE bill_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);
            statement.setInt(2, admissionId);
            statement.setBigDecimal(3, roomCharges);
            statement.setBigDecimal(4, doctorCharges);
            statement.setBigDecimal(5, medicineCharges);
            statement.setBigDecimal(6, otherCharges);
            statement.setBigDecimal(7, totalAmount);
            statement.setBigDecimal(8, paidAmount);
            statement.setString(9, paymentStatus);
            statement.setInt(10, billId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
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
                        resultSet.getInt("admission_id"),
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
                            resultSet.getInt("admission_id"),
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
                            resultSet.getInt("admission_id"),
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

        if (paymentAmount == null || paymentAmount.signum() <= 0) {
            return false;
        }

        String selectSql =
                "SELECT total_amount, paid_amount " +
                "FROM bills " +
                "WHERE bill_id = ?";

        String updateSql =
                "UPDATE bills SET " +
                "paid_amount = ?, " +
                "payment_status = ? " +
                "WHERE bill_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement selectStatement =
                        connection.prepareStatement(selectSql)
        ) {

            selectStatement.setInt(1, billId);

            try (ResultSet resultSet =
                         selectStatement.executeQuery()) {

                if (!resultSet.next()) {
                    return false;
                }

                BigDecimal totalAmount =
                        resultSet.getBigDecimal("total_amount");

                BigDecimal oldPaidAmount =
                        resultSet.getBigDecimal("paid_amount");

                BigDecimal remainingAmount =
                        totalAmount.subtract(oldPaidAmount);

                // Prevent overpayment
                if (paymentAmount.compareTo(remainingAmount) > 0) {
                    return false;
                }

                BigDecimal newPaidAmount =
                        oldPaidAmount.add(paymentAmount);

                String paymentStatus =
                        getPaymentStatus(
                                totalAmount,
                                newPaidAmount
                        );

                try (
                        PreparedStatement updateStatement =
                                connection.prepareStatement(updateSql)
                ) {

                    updateStatement.setBigDecimal(
                            1,
                            newPaidAmount
                    );

                    updateStatement.setString(
                            2,
                            paymentStatus
                    );

                    updateStatement.setInt(
                            3,
                            billId
                    );

                    return updateStatement.executeUpdate() > 0;
                }
            }

        } catch (Exception e) {
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

    // =========================================================
    // PAYMENT STATUS
    // =========================================================

    private String getPaymentStatus(
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

        return roomCharges.add(doctorCharges).add(medicineCharges).add(otherCharges);
    }
}
