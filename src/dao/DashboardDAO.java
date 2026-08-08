package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAO {

    public int getTotalPatients() {
        return getCount("SELECT COUNT(*) FROM patients");
    }

    public int getTotalDoctors() {
        return getCount("SELECT COUNT(*) FROM doctors");
    }

    public int getTotalAppointments() {
        return getCount("SELECT COUNT(*) FROM appointments");
    }

    public int getActiveAdmissions() {
        return getCount(
            "SELECT COUNT(*) FROM admissions WHERE discharge_date IS NULL"
        );
    }

    public int getAvailableBeds() {
        return getCount(
            "SELECT COUNT(*) FROM beds WHERE status = 'AVAILABLE'"
        );
    }

    public double getTotalRevenue() {

        String sql =
            "SELECT COALESCE(SUM(total_amount), 0) " +
            "FROM invoices WHERE payment_status = 'PAID'";

        try (Connection connection =
                    DatabaseConnection.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(sql);
             ResultSet resultSet =
                    statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private int getCount(String sql) {

        try (Connection connection =
                    DatabaseConnection.getConnection();
             PreparedStatement statement =
                    connection.prepareStatement(sql);
             ResultSet resultSet =
                    statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}