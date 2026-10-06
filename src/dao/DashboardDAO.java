package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DashboardDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardDAO.class);

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
            LOGGER.error("Unable to load dashboard count.", e);
            throw new IllegalStateException("Unable to load dashboard information.", e);
        }
        throw new IllegalStateException("Dashboard count query returned no result.");
    }
}
