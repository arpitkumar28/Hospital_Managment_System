package dao;

import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppointmentDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentDAO.class);

    // ================= ADD =================

    public boolean addAppointment(
            int patientId,
            int doctorId,
            String date,
            String time,
            String reason,
            String status) {

        String sql =
                "INSERT INTO appointments " +
                "(patient_id, doctor_id, appointment_date, " +
                "appointment_time, reason, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);
            statement.setDate(
                    3,
                    Date.valueOf(date)
            );
            statement.setTime(
                    4,
                    Time.valueOf(time)
            );
            statement.setString(5, reason);
            statement.setString(6, status);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Unable to save appointment.", e);
            throw new IllegalStateException("Unable to save appointment.", e);
        }
    }


    // ================= GET ALL =================

    public List<Object[]> getAllAppointments() {

        List<Object[]> appointments =
                new ArrayList<>();

        String sql =
                "SELECT " +
                "a.appointment_id, " +
                "p.name AS patient_name, " +
                "d.name AS doctor_name, " +
                "d.specialization, " +
                "a.appointment_date, " +
                "a.appointment_time, " +
                "a.reason, " +
                "a.status " +
                "FROM appointments a " +
                "JOIN patients p " +
                "ON a.patient_id = p.patient_id " +
                "JOIN doctors d " +
                "ON a.doctor_id = d.doctor_id " +
                "ORDER BY a.appointment_date DESC, " +
                "a.appointment_time DESC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                appointments.add(
                        new Object[]{
                                resultSet.getInt(
                                        "appointment_id"
                                ),

                                resultSet.getString(
                                        "patient_name"
                                ),

                                resultSet.getString(
                                        "doctor_name"
                                ),

                                resultSet.getString(
                                        "specialization"
                                ),

                                resultSet.getDate(
                                        "appointment_date"
                                ),

                                resultSet.getTime(
                                        "appointment_time"
                                ),

                                resultSet.getString(
                                        "reason"
                                ),

                                resultSet.getString(
                                        "status"
                                )
                        }
                );
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to load appointments.", e);
            throw new IllegalStateException("Unable to load appointment records.", e);
        }

        return appointments;
    }


    // ================= UPDATE =================

    public boolean updateAppointment(
            int appointmentId,
            int patientId,
            int doctorId,
            String date,
            String time,
            String reason,
            String status) {

        String sql =
                "UPDATE appointments SET " +
                "patient_id = ?, " +
                "doctor_id = ?, " +
                "appointment_date = ?, " +
                "appointment_time = ?, " +
                "reason = ?, " +
                "status = ? " +
                "WHERE appointment_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);
            statement.setDate(
                    3,
                    Date.valueOf(date)
            );
            statement.setTime(
                    4,
                    Time.valueOf(time)
            );
            statement.setString(5, reason);
            statement.setString(6, status);
            statement.setInt(7, appointmentId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Unable to update appointment {}.", appointmentId, e);
            throw new IllegalStateException("Unable to update appointment.", e);
        }
    }


    // ================= DELETE =================

    public boolean deleteAppointment(
            int appointmentId) {

        String sql =
                "DELETE FROM appointments " +
                "WHERE appointment_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    appointmentId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.error("Unable to delete appointment {}.", appointmentId, e);
            throw new IllegalStateException("Unable to delete appointment.", e);
        }
    }
}