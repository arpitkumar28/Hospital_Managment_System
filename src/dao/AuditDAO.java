package dao;

import database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Stores security events without passwords, password hashes or session tokens. */
public class AuditDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditDAO.class);

    public boolean recordEvent(Long userId, String eventType) {
        String sql = "INSERT INTO audit_logs (user_id, event_type) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (userId == null) statement.setNull(1, java.sql.Types.BIGINT);
            else statement.setLong(1, userId);
            statement.setString(2, eventType);
            statement.executeUpdate();
            return true;
        } catch (SQLException exception) {
            LOGGER.error("Unable to persist authentication audit event {}.", eventType, exception);
            return false;
        }
    }
}
