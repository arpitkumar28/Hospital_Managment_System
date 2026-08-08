package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public String login(String username, String password) {

        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("role");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}