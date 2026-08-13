package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    // =========================================================
    // ADD BILL
    // =========================================================

    public boolean addBill(
            int patientId,
            int admissionId,
            double roomCharges,
            double doctorCharges,
            double medicineCharges,
            double otherCharges,
            double paidAmount
    ) {

        double totalAmount =
                roomCharges
                + doctorCharges
                + medicineCharges
                + otherCharges;

        if (paidAmount < 0 || paidAmount > totalAmount) {
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
            statement.setDouble(3, roomCharges);
            statement.setDouble(4, doctorCharges);
            statement.setDouble(5, medicineCharges);
            statement.setDouble(6, otherCharges);
            statement.setDouble(7, totalAmount);
            statement.setDouble(8, paidAmount);
            statement.setString(9, paymentStatus);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // UPDATE BILL
    // =========================================================

    public boolean updateBill(
            int billId,
            int patientId,
            int admissionId,
            double roomCharges,
            double doctorCharges,
            double medicineCharges,
            double otherCharges,
            double paidAmount
    ) {

        double totalAmount =
                roomCharges
                + doctorCharges
                + medicineCharges
                + otherCharges;

        if (paidAmount < 0 || paidAmount > totalAmount) {
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
            statement.setDouble(3, roomCharges);
            statement.setDouble(4, doctorCharges);
            statement.setDouble(5, medicineCharges);
            statement.setDouble(6, otherCharges);
            statement.setDouble(7, totalAmount);
            statement.setDouble(8, paidAmount);
            statement.setString(9, paymentStatus);
            statement.setInt(10, billId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
            e.printStackTrace();
            return false;
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
                        resultSet.getDouble("room_charges"),
                        resultSet.getDouble("doctor_charges"),
                        resultSet.getDouble("medicine_charges"),
                        resultSet.getDouble("other_charges"),
                        resultSet.getDouble("total_amount"),
                        resultSet.getDouble("paid_amount"),
                        resultSet.getString("payment_status"),
                        resultSet.getTimestamp("bill_date")
                };

                bills.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                            resultSet.getDouble("room_charges"),
                            resultSet.getDouble("doctor_charges"),
                            resultSet.getDouble("medicine_charges"),
                            resultSet.getDouble("other_charges"),
                            resultSet.getDouble("total_amount"),
                            resultSet.getDouble("paid_amount"),
                            resultSet.getString("payment_status"),
                            resultSet.getTimestamp("bill_date")
                    };

                    bills.add(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                            resultSet.getDouble("room_charges"),
                            resultSet.getDouble("doctor_charges"),
                            resultSet.getDouble("medicine_charges"),
                            resultSet.getDouble("other_charges"),
                            resultSet.getDouble("total_amount"),
                            resultSet.getDouble("paid_amount"),
                            resultSet.getString("payment_status"),
                            resultSet.getTimestamp("bill_date")
                    };
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // ADD PAYMENT
    // =========================================================

    public boolean addPayment(
            int billId,
            double paymentAmount
    ) {

        if (paymentAmount <= 0) {
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

                double totalAmount =
                        resultSet.getDouble("total_amount");

                double oldPaidAmount =
                        resultSet.getDouble("paid_amount");

                double remainingAmount =
                        totalAmount - oldPaidAmount;

                // Prevent overpayment
                if (paymentAmount > remainingAmount) {
                    return false;
                }

                double newPaidAmount =
                        oldPaidAmount + paymentAmount;

                String paymentStatus =
                        getPaymentStatus(
                                totalAmount,
                                newPaidAmount
                        );

                try (
                        PreparedStatement updateStatement =
                                connection.prepareStatement(updateSql)
                ) {

                    updateStatement.setDouble(
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
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // GET REMAINING AMOUNT
    // =========================================================

    public double getRemainingAmount(int billId) {

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

                    double total =
                            resultSet.getDouble("total_amount");

                    double paid =
                            resultSet.getDouble("paid_amount");

                    return total - paid;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // PAYMENT STATUS
    // =========================================================

    private String getPaymentStatus(
            double totalAmount,
            double paidAmount
    ) {

        if (paidAmount <= 0) {
            return "PENDING";

        } else if (paidAmount >= totalAmount) {
            return "PAID";

        } else {
            return "PARTIAL";
        }
    }

    // =========================================================
    // TOTAL BILL
    // =========================================================

    public double calculateTotal(
            double roomCharges,
            double doctorCharges,
            double medicineCharges,
            double otherCharges
    ) {

        return roomCharges
                + doctorCharges
                + medicineCharges
                + otherCharges;
    }
}