package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // =====================================================
    // GET ALL ROOMS
    // =====================================================

    public List<Object[]> getAllRooms() {

        List<Object[]> rooms = new ArrayList<>();

        String sql =
                "SELECT room_id, room_number, room_type, price_per_day " +
                "FROM rooms " +
                "ORDER BY room_id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Object[] room = {

                        resultSet.getInt(
                                "room_id"
                        ),

                        resultSet.getString(
                                "room_number"
                        ),

                        resultSet.getString(
                                "room_type"
                        ),

                        resultSet.getDouble(
                                "price_per_day"
                        )
                };

                rooms.add(room);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return rooms;
    }


    // =====================================================
    // ADD ROOM
    // =====================================================

    public boolean addRoom(
            String roomNumber,
            String roomType,
            double pricePerDay) {

        String sql =
                "INSERT INTO rooms " +
                "(room_number, room_type, price_per_day) " +
                "VALUES (?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    roomNumber
            );

            statement.setString(
                    2,
                    roomType
            );

            statement.setDouble(
                    3,
                    pricePerDay
            );

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (
                SQLIntegrityConstraintViolationException e
        ) {

            System.out.println(
                    "Room number already exists."
            );

            return false;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // UPDATE ROOM
    // =====================================================

    public boolean updateRoom(
            int roomId,
            String roomNumber,
            String roomType,
            double pricePerDay) {

        String sql =
                "UPDATE rooms SET " +
                "room_number = ?, " +
                "room_type = ?, " +
                "price_per_day = ? " +
                "WHERE room_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    roomNumber
            );

            statement.setString(
                    2,
                    roomType
            );

            statement.setDouble(
                    3,
                    pricePerDay
            );

            statement.setInt(
                    4,
                    roomId
            );

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (
                SQLIntegrityConstraintViolationException e
        ) {

            System.out.println(
                    "Room number already exists."
            );

            return false;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // DELETE ROOM
    // =====================================================

    public boolean deleteRoom(
            int roomId) {

        String sql =
                "DELETE FROM rooms " +
                "WHERE room_id = ?";

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

            int rows =
                    statement.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // CHECK ROOM NUMBER
    // =====================================================

    public boolean roomExists(
            String roomNumber) {

        String sql =
                "SELECT room_id " +
                "FROM rooms " +
                "WHERE room_number = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    roomNumber
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                return resultSet.next();
            }

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =====================================================
    // GET ROOM COUNT
    // =====================================================

    public int getRoomCount() {

        String sql =
                "SELECT COUNT(*) FROM rooms";

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