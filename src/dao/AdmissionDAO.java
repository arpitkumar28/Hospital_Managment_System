package dao;

import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdmissionDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdmissionDAO.class);

    // =====================================================
    // GET PATIENTS
    // =====================================================

    public List<Object[]> getPatients() {

        List<Object[]> patients = new ArrayList<>();

        String sql =
                "SELECT patient_id, name " +
                "FROM patients " +
                "ORDER BY patient_id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        statement.executeQuery()
        ) {

            while (rs.next()) {

                patients.add(
                        new Object[]{
                                rs.getInt("patient_id"),
                                rs.getString("name")
                        }
                );
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to load patients for admissions.", e);
            throw new IllegalStateException("Unable to load patient choices.", e);
        }

        return patients;
    }


    // =====================================================
    // GET AVAILABLE BEDS
    // =====================================================

    public List<Object[]> getAvailableBeds() {

        List<Object[]> beds = new ArrayList<>();

        String sql =
                "SELECT b.bed_id, " +
                "b.bed_number, " +
                "r.room_number, " +
                "r.room_type " +
                "FROM beds b " +
                "JOIN rooms r ON b.room_id = r.room_id " +
                "WHERE b.status = 'AVAILABLE' " +
                "ORDER BY r.room_number, b.bed_number";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        statement.executeQuery()
        ) {

            while (rs.next()) {

                beds.add(
                        new Object[]{
                                rs.getInt("bed_id"),
                                rs.getString("bed_number"),
                                rs.getString("room_number"),
                                rs.getString("room_type")
                        }
                );
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to load available beds for admissions.", e);
            throw new IllegalStateException("Unable to load available bed choices.", e);
        }

        return beds;
    }


    // =====================================================
    // GET ALL ADMISSIONS
    // =====================================================

    public List<Object[]> getAllAdmissions() {

        List<Object[]> admissions =
                new ArrayList<>();

        String sql =
                "SELECT a.admission_id, " +
                "p.name AS patient_name, " +
                "b.bed_number, " +
                "r.room_number, " +
                "a.admission_date, " +
                "a.discharge_date " +
                "FROM admissions a " +
                "JOIN patients p " +
                "ON a.patient_id = p.patient_id " +
                "JOIN beds b " +
                "ON a.bed_id = b.bed_id " +
                "JOIN rooms r " +
                "ON b.room_id = r.room_id " +
                "ORDER BY a.admission_id DESC";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        statement.executeQuery()
        ) {

            while (rs.next()) {

                admissions.add(
                        new Object[]{
                                rs.getInt("admission_id"),
                                rs.getString("patient_name"),
                                rs.getString("bed_number"),
                                rs.getString("room_number"),
                                rs.getDate("admission_date"),
                                rs.getDate("discharge_date")
                        }
                );
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to load admissions.", e);
            throw new IllegalStateException("Unable to load admission records.", e);
        }

        return admissions;
    }


    // =====================================================
    // ADMIT PATIENT
    // =====================================================

    public boolean admitPatient(
            int patientId,
            int bedId,
            Date admissionDate) {

        if (admissionDate == null) {
            throw new IllegalArgumentException("Admission date is required.");
        }

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);


            // ---------------------------------------------
            // Check bed availability
            // ---------------------------------------------

            String checkSql =
                    "SELECT status " +
                    "FROM beds " +
                    "WHERE bed_id = ? " +
                    "FOR UPDATE";

            try (
                    PreparedStatement checkStatement =
                            connection.prepareStatement(
                                    checkSql
                            )
            ) {

                checkStatement.setInt(
                        1,
                        bedId
                );

                try (
                        ResultSet rs =
                                checkStatement.executeQuery()
                ) {

                    if (!rs.next()) {

                        connection.rollback();

                        return false;
                    }

                    String status =
                            rs.getString("status");

                    if (!"AVAILABLE".equals(
                            status
                    )) {

                        connection.rollback();

                        return false;
                    }
                }
            }

            String activeAdmissionSql = "SELECT 1 FROM admissions WHERE bed_id = ? AND discharge_date IS NULL LIMIT 1";
            try (PreparedStatement statement = connection.prepareStatement(activeAdmissionSql)) {
                statement.setInt(1, bedId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // ---------------------------------------------
            // Insert admission
            // ---------------------------------------------

            String admissionSql =
                    "INSERT INTO admissions " +
                    "(patient_id, bed_id, admission_date) " +
                    "VALUES (?, ?, ?)";

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    admissionSql
                            )
            ) {

                statement.setInt(
                        1,
                        patientId
                );

                statement.setInt(
                        2,
                        bedId
                );

                statement.setDate(
                        3,
                        admissionDate
                );

                statement.executeUpdate();
            }


            // ---------------------------------------------
            // Mark bed occupied
            // ---------------------------------------------

            String bedSql =
                    "UPDATE beds " +
                    "SET status = 'OCCUPIED' " +
                    "WHERE bed_id = ?";

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    bedSql
                            )
            ) {

                statement.setInt(
                        1,
                        bedId
                );

                statement.executeUpdate();
            }


            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackFailure) {
                    e.addSuppressed(rollbackFailure);
                }
            }

            LOGGER.error("Unable to admit patient {} to bed {}.", patientId, bedId, e);
            throw new IllegalStateException("Unable to admit patient.", e);

        } finally {

            if (connection != null) {

                try {
                    connection.close();
                } catch (SQLException closeFailure) {
                    LOGGER.warn("Unable to close admission connection.", closeFailure);
                }
            }
        }
    }


    // =====================================================
    // DISCHARGE PATIENT
    // =====================================================

    public boolean dischargePatient(
            int admissionId,
            Date dischargeDate) {

        if (dischargeDate == null) {
            throw new IllegalArgumentException("Discharge date is required.");
        }

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);


            // ---------------------------------------------
            // Find bed
            // ---------------------------------------------

            int bedId;
            Date admissionDate;

            String findSql =
                    "SELECT bed_id, admission_date " +
                    "FROM admissions " +
                    "WHERE admission_id = ? " +
                    "AND discharge_date IS NULL " +
                    "FOR UPDATE";

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    findSql
                            )
            ) {

                statement.setInt(
                        1,
                        admissionId
                );

                try (
                        ResultSet rs =
                                statement.executeQuery()
                ) {

                    if (!rs.next()) {

                        connection.rollback();

                        return false;
                    }

                    bedId =
                            rs.getInt(
                                    "bed_id"
                            );
                    admissionDate = rs.getDate("admission_date");
                }
            }

            if (dischargeDate.before(admissionDate)) {
                connection.rollback();
                return false;
            }


            // ---------------------------------------------
            // Set discharge date
            // ---------------------------------------------

            String admissionSql =
                    "UPDATE admissions " +
                    "SET discharge_date = ? " +
                    "WHERE admission_id = ? " +
                    "AND discharge_date IS NULL";

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    admissionSql
                            )
            ) {

                statement.setDate(
                        1,
                        dischargeDate
                );

                statement.setInt(
                        2,
                        admissionId
                );

                if (statement.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }
            }


            // ---------------------------------------------
            // Make bed available
            // ---------------------------------------------

            String bedSql =
                    "UPDATE beds " +
                    "SET status = 'AVAILABLE' " +
                    "WHERE bed_id = ?";

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    bedSql
                            )
            ) {

                statement.setInt(
                        1,
                        bedId
                );

                if (statement.executeUpdate() != 1) {
                    connection.rollback();
                    return false;
                }
            }


            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackFailure) {
                    e.addSuppressed(rollbackFailure);
                }
            }

            LOGGER.error("Unable to discharge admission {}.", admissionId, e);
            throw new IllegalStateException("Unable to discharge admission.", e);

        } finally {

            if (connection != null) {

                try {
                    connection.close();
                } catch (SQLException closeFailure) {
                    LOGGER.warn("Unable to close admission connection.", closeFailure);
                }
            }
        }
    }
}
