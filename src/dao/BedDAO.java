package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(BedDAO.class);

    // =====================================================
    // GET ALL BEDS
    // =====================================================

    public List<Object[]> getAllBeds() {

        List<Object[]> beds = new ArrayList<>();

        String sql =
                "SELECT b.bed_id, b.room_id, r.room_number, " +
                "b.bed_number, b.status " +
                "FROM beds b " +
                "JOIN rooms r ON b.room_id = r.room_id " +
                "ORDER BY b.bed_id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Object[] bed = {

                        resultSet.getInt("bed_id"),

                        resultSet.getInt("room_id"),

                        resultSet.getString("room_number"),

                        resultSet.getString("bed_number"),

                        resultSet.getString("status")
                };

                beds.add(bed);
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to load beds.", e);
            throw new IllegalStateException("Unable to load bed records.", e);
        }

        return beds;
    }


    // =====================================================
    // GET AVAILABLE BEDS
    // =====================================================

    public List<Object[]> getAvailableBeds() {

        List<Object[]> beds = new ArrayList<>();

        String sql =
                "SELECT b.bed_id, b.room_id, r.room_number, " +
                "b.bed_number, b.status " +
                "FROM beds b " +
                "JOIN rooms r ON b.room_id = r.room_id " +
                "WHERE b.status = 'AVAILABLE' " +
                "AND NOT EXISTS (SELECT 1 FROM admissions a " +
                "WHERE a.bed_id = b.bed_id AND a.discharge_date IS NULL) " +
                "ORDER BY b.bed_id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Object[] bed = {

                        resultSet.getInt("bed_id"),

                        resultSet.getInt("room_id"),

                        resultSet.getString("room_number"),

                        resultSet.getString("bed_number"),

                        resultSet.getString("status")
                };

                beds.add(bed);
            }

        } catch (SQLException e) {
            LOGGER.error("Unable to load available beds.", e);
            throw new IllegalStateException("Unable to load available beds.", e);
        }

        return beds;
    }


    // =====================================================
    // ADD BED
    // =====================================================

    public boolean addBed(
            int roomId,
            String bedNumber) {

        if (roomId <= 0 || !isValidBedNumber(bedNumber)) {
            return false;
        }

        String sql =
                "INSERT INTO beds " +
                "(room_id, bed_number, status) " +
                "VALUES (?, ?, 'AVAILABLE')";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    roomId
            );

            statement.setString(2, bedNumber.trim());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            if (isConstraintViolation(e)) {
                LOGGER.warn("Unable to add bed because a room reference or unique bed number conflicts.");
                return false;
            }
            LOGGER.error("Unable to add bed.", e);
            throw new IllegalStateException("Unable to save bed.", e);
        }
    }


    // =====================================================
    // UPDATE BED
    // =====================================================

    public boolean updateBed(
            int bedId,
            int roomId,
            String bedNumber,
            String status) {

        if (bedId <= 0 || roomId <= 0 || !isValidBedNumber(bedNumber) || !isValidStatus(status)) {
            return false;
        }

        String sql =
                "UPDATE beds SET " +
                "room_id = ?, " +
                "bed_number = ?, " +
                "status = ? " +
                "WHERE bed_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT room_id, bed_number, status FROM beds WHERE bed_id = ? FOR UPDATE")) {
                    lock.setInt(1, bedId);
                    try (ResultSet current = lock.executeQuery()) {
                        if (!current.next()) {
                            connection.rollback();
                            return false;
                        }
                        boolean activeAdmission = hasActiveAdmission(connection, bedId);
                        boolean unchangedOccupiedBed = current.getInt("room_id") == roomId
                                && current.getString("bed_number").equals(bedNumber.trim())
                                && "OCCUPIED".equals(status);
                        if (activeAdmission && !unchangedOccupiedBed) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, roomId);
                    statement.setString(2, bedNumber.trim());
                    statement.setString(3, status);
                    statement.setInt(4, bedId);
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException transactionFailure) {
                rollback(connection, transactionFailure);
                throw transactionFailure;
            }
        } catch (SQLException e) {
            if (isConstraintViolation(e)) return false;
            LOGGER.error("Unable to update bed {}.", bedId, e);
            throw new IllegalStateException("Unable to update bed.", e);
        }
    }


    // =====================================================
    // UPDATE BED STATUS
    // =====================================================

    public boolean updateBedStatus(
            int bedId,
            String status) {

        if (bedId <= 0 || !isValidStatus(status)) {
            return false;
        }

        String sql =
                "UPDATE beds SET status = ? " +
                "WHERE bed_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement lock = connection.prepareStatement(
                        "SELECT status FROM beds WHERE bed_id = ? FOR UPDATE")) {
                    lock.setInt(1, bedId);
                    try (ResultSet current = lock.executeQuery()) {
                        if (!current.next()) {
                            connection.rollback();
                            return false;
                        }
                        if (!"OCCUPIED".equals(status) && hasActiveAdmission(connection, bedId)) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, status);
                    statement.setInt(2, bedId);
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }
                connection.commit();
                return true;
            } catch (SQLException transactionFailure) {
                rollback(connection, transactionFailure);
                throw transactionFailure;
            }
        } catch (SQLException e) {
            if (isConstraintViolation(e)) return false;
            LOGGER.error("Unable to update status for bed {}.", bedId, e);
            throw new IllegalStateException("Unable to update bed status.", e);
        }
    }


    // =====================================================
    // DELETE BED
    // =====================================================

    public boolean deleteBed(
            int bedId) {

        if (bedId <= 0) {
            return false;
        }

        String sql =
                "DELETE FROM beds " +
                "WHERE bed_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    bedId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            if (isConstraintViolation(e)) return false;
            LOGGER.error("Unable to delete bed {}.", bedId, e);
            throw new IllegalStateException("Unable to delete bed. It may have an active admission.", e);
        }
    }


    // =====================================================
    // GET BED COUNT
    // =====================================================

    public int getBedCount() {

        String sql =
                "SELECT COUNT(*) FROM beds";

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

        } catch (SQLException e) {
            LOGGER.error("Unable to load bed count.", e);
            throw new IllegalStateException("Unable to load bed count.", e);
        }
        throw new IllegalStateException("Bed count query returned no result.");
    }


    // =====================================================
    // GET AVAILABLE BED COUNT
    // =====================================================

    public int getAvailableBedCount() {

        String sql =
                "SELECT COUNT(*) FROM beds " +
                "WHERE status = 'AVAILABLE' " +
                "AND NOT EXISTS (SELECT 1 FROM admissions a " +
                "WHERE a.bed_id = beds.bed_id AND a.discharge_date IS NULL)";

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

        } catch (SQLException e) {
            LOGGER.error("Unable to load available bed count.", e);
            throw new IllegalStateException("Unable to load available bed count.", e);
        }
        throw new IllegalStateException("Available bed count query returned no result.");
    }


    // =====================================================
    // GET OCCUPIED BED COUNT
    // =====================================================

    public int getOccupiedBedCount() {

        String sql =
                "SELECT COUNT(*) FROM beds " +
                "WHERE status = 'OCCUPIED'";

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

        } catch (SQLException e) {
            LOGGER.error("Unable to load occupied bed count.", e);
            throw new IllegalStateException("Unable to load occupied bed count.", e);
        }
        throw new IllegalStateException("Occupied bed count query returned no result.");
    }

    static boolean isValidBedNumber(String bedNumber) {
        return bedNumber != null && !bedNumber.isBlank() && bedNumber.trim().length() <= 40;
    }

    static boolean isValidStatus(String status) {
        return "AVAILABLE".equals(status) || "OCCUPIED".equals(status);
    }

    private static boolean hasActiveAdmission(Connection connection, int bedId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM admissions WHERE bed_id = ? AND discharge_date IS NULL LIMIT 1")) {
            statement.setInt(1, bedId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private static boolean isConstraintViolation(SQLException exception) {
        for (SQLException current = exception; current != null; current = current.getNextException()) {
            String state = current.getSQLState();
            if (state != null && state.startsWith("23")) return true;
        }
        return false;
    }

    private static void rollback(Connection connection, SQLException exception) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            exception.addSuppressed(rollbackFailure);
        }
    }
}
