package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class BedDAO {

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

            e.printStackTrace();
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

            e.printStackTrace();
        }

        return beds;
    }


    // =====================================================
    // ADD BED
    // =====================================================

    public boolean addBed(
            int roomId,
            String bedNumber) {

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

            statement.setString(
                    2,
                    bedNumber
            );

            return statement.executeUpdate() > 0;

        } catch (
                SQLIntegrityConstraintViolationException e
        ) {

            System.out.println(
                    "Unable to add bed. " +
                    "Check room ID or duplicate data."
            );

            return false;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
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

        String sql =
                "UPDATE beds SET " +
                "room_id = ?, " +
                "bed_number = ?, " +
                "status = ? " +
                "WHERE bed_id = ?";

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

            statement.setString(
                    2,
                    bedNumber
            );

            statement.setString(
                    3,
                    status
            );

            statement.setInt(
                    4,
                    bedId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // UPDATE BED STATUS
    // =====================================================

    public boolean updateBedStatus(
            int bedId,
            String status) {

        String sql =
                "UPDATE beds SET status = ? " +
                "WHERE bed_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    status
            );

            statement.setInt(
                    2,
                    bedId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // DELETE BED
    // =====================================================

    public boolean deleteBed(
            int bedId) {

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

            e.printStackTrace();

            return false;
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

            e.printStackTrace();
        }

        return 0;
    }


    // =====================================================
    // GET AVAILABLE BED COUNT
    // =====================================================

    public int getAvailableBedCount() {

        String sql =
                "SELECT COUNT(*) FROM beds " +
                "WHERE status = 'AVAILABLE'";

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

            e.printStackTrace();
        }

        return 0;
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

            e.printStackTrace();
        }

        return 0;
    }
}