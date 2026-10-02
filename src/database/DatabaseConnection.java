package database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        return ConnectionPool.getConnection();
    }
}
