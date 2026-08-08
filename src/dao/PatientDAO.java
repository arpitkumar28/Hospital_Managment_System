package dao;

import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // ================= ADD PATIENT =================

    public boolean addPatient(
            String name,
            String gender,
            String dob,
            String phone,
            String email,
            String address,
            String bloodGroup,
            String emergencyContact) {

        String sql = """
                INSERT INTO patients
                (name, gender, date_of_birth, phone, email,
                 address, blood_group, emergency_contact)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, gender);

            if (dob.isEmpty()) {
                statement.setNull(3, Types.DATE);
            } else {
                statement.setDate(
                        3,
                        Date.valueOf(dob)
                );
            }

            statement.setString(4, phone);
            statement.setString(5, email);
            statement.setString(6, address);
            statement.setString(7, bloodGroup);
            statement.setString(8, emergencyContact);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ================= GET ALL PATIENTS =================

    public List<Object[]> getAllPatients() {

        List<Object[]> patients = new ArrayList<>();

        String sql =
                "SELECT patient_id, name, gender, date_of_birth, " +
                "phone, email, address, blood_group, " +
                "emergency_contact FROM patients " +
                "ORDER BY patient_id DESC";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                patients.add(new Object[]{
                        resultSet.getInt("patient_id"),
                        resultSet.getString("name"),
                        resultSet.getString("gender"),
                        resultSet.getDate("date_of_birth"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("address"),
                        resultSet.getString("blood_group"),
                        resultSet.getString("emergency_contact")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patients;
    }


    // ================= UPDATE PATIENT =================

    public boolean updatePatient(
            int patientId,
            String name,
            String gender,
            String dob,
            String phone,
            String email,
            String address,
            String bloodGroup,
            String emergencyContact) {

        String sql = """
                UPDATE patients
                SET name = ?,
                    gender = ?,
                    date_of_birth = ?,
                    phone = ?,
                    email = ?,
                    address = ?,
                    blood_group = ?,
                    emergency_contact = ?
                WHERE patient_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, gender);

            if (dob.isEmpty()) {
                statement.setNull(3, Types.DATE);
            } else {
                statement.setDate(
                        3,
                        Date.valueOf(dob)
                );
            }

            statement.setString(4, phone);
            statement.setString(5, email);
            statement.setString(6, address);
            statement.setString(7, bloodGroup);
            statement.setString(8, emergencyContact);
            statement.setInt(9, patientId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // ================= DELETE PATIENT =================

    public boolean deletePatient(int patientId) {

        String sql =
                "DELETE FROM patients WHERE patient_id = ?";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, patientId);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}