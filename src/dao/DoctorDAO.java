package dao;

import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoctorDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDAO.class);

    // ================= ADD DOCTOR =================

    public boolean addDoctor(
            String name,
            String specialization,
            String phone,
            String email,
            String experience,
            String consultationFee,
            String availability) {

        String sql = """
                INSERT INTO doctors
                (name, specialization, phone, email,
                 experience, consultation_fee, availability)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, specialization);
            statement.setString(3, phone);
            statement.setString(4, email);
            statement.setInt(5, Integer.parseInt(experience));
            statement.setDouble(6, Double.parseDouble(consultationFee));
            statement.setString(7, availability);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.error("Unable to add doctor.", e);
            throw new IllegalStateException("Unable to save doctor record.", e);
        }
    }


    // ================= GET ALL DOCTORS =================

    public List<Object[]> getAllDoctors() {

        List<Object[]> doctors = new ArrayList<>();

        String sql = """
                SELECT doctor_id, name, specialization,
                       phone, email, experience,
                       consultation_fee, availability
                FROM doctors
                ORDER BY doctor_id DESC
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                doctors.add(new Object[]{
                        resultSet.getInt("doctor_id"),
                        resultSet.getString("name"),
                        resultSet.getString("specialization"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getInt("experience"),
                        resultSet.getDouble("consultation_fee"),
                        resultSet.getString("availability")
                });
            }

        } catch (Exception e) {
            LOGGER.error("Unable to load doctors.", e);
            throw new IllegalStateException("Unable to load doctor records.", e);
        }

        return doctors;
    }


    // ================= UPDATE DOCTOR =================

    public boolean updateDoctor(
            int doctorId,
            String name,
            String specialization,
            String phone,
            String email,
            String experience,
            String consultationFee,
            String availability) {

        String sql = """
                UPDATE doctors
                SET name = ?,
                    specialization = ?,
                    phone = ?,
                    email = ?,
                    experience = ?,
                    consultation_fee = ?,
                    availability = ?
                WHERE doctor_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, specialization);
            statement.setString(3, phone);
            statement.setString(4, email);
            statement.setInt(5, Integer.parseInt(experience));
            statement.setDouble(6, Double.parseDouble(consultationFee));
            statement.setString(7, availability);
            statement.setInt(8, doctorId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.error("Unable to update doctor {}.", doctorId, e);
            throw new IllegalStateException("Unable to update doctor record.", e);
        }
    }


    // ================= DELETE DOCTOR =================

    public boolean deleteDoctor(int doctorId) {

        String sql =
                "DELETE FROM doctors WHERE doctor_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, doctorId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            LOGGER.error("Unable to delete doctor {}.", doctorId, e);
            throw new IllegalStateException("Unable to delete doctor record. It may have related appointments.", e);
        }
    }
}
