package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAO {

    // =====================================================
    // TOTAL PATIENTS
    // =====================================================

    public int getTotalPatients() {

        String sql =
                "SELECT COUNT(*) FROM patients";

        return getCount(sql);
    }


    // =====================================================
    // TOTAL DOCTORS
    // =====================================================

    public int getTotalDoctors() {

        String sql =
                "SELECT COUNT(*) FROM doctors";

        return getCount(sql);
    }


    // =====================================================
    // TOTAL APPOINTMENTS
    // =====================================================

    public int getTotalAppointments() {

        String sql =
                "SELECT COUNT(*) FROM appointments";

        return getCount(sql);
    }


    // =====================================================
    // PENDING APPOINTMENTS
    // =====================================================

    public int getPendingAppointments() {

        String sql =
                "SELECT COUNT(*) " +
                "FROM appointments " +
                "WHERE status = 'PENDING'";

        return getCount(sql);
    }


    // =====================================================
    // COMMON COUNT METHOD
    // =====================================================

    private int getCount(String sql) {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return resultSet.getInt(1);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }
}