package dao;

import database.DatabaseConnection;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

/** PostgreSQL access for authenticated staff identities. All inputs are bound. */
public class UserDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) throws SQLException {
        String sql = "SELECT user_id, username, email, password_hash, full_name, phone, role, status, "
                + "failed_login_attempts, locked_until FROM users "
                + "WHERE lower(username) = lower(?) OR lower(email) = lower(?) ORDER BY user_id LIMIT 2";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usernameOrEmail.trim());
            statement.setString(2, usernameOrEmail.trim());
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return Optional.empty();
                User found = map(result);
                if (result.next()) {
                    LOGGER.warn("Ambiguous username/email login identifier rejected.");
                    return Optional.empty();
                }
                return Optional.of(found);
            }
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        return findByColumn("username", username);
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        return findByColumn("email", email);
    }

    private Optional<User> findByColumn(String column, String value) throws SQLException {
        if (!column.equals("username") && !column.equals("email")) throw new IllegalArgumentException("Unsupported user lookup.");
        String sql = "SELECT user_id, username, email, password_hash, full_name, phone, role, status, "
                + "failed_login_attempts, locked_until FROM users WHERE lower(" + column + ") = lower(?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public boolean existsByUsername(String username) throws SQLException {
        return exists("username", username);
    }

    public boolean existsByEmail(String email) throws SQLException {
        return exists("email", email);
    }

    private boolean exists(String column, String value) throws SQLException {
        if (!column.equals("username") && !column.equals("email")) throw new IllegalArgumentException("Unsupported user lookup.");
        String sql = "SELECT 1 FROM users WHERE lower(" + column + ") = lower(?) LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    public long createUser(String fullName, String username, String email, String phone,
                           String passwordHash, UserRole role, UserStatus status) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, phone, role, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING user_id";
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username.trim());
                statement.setString(2, email.trim().toLowerCase(java.util.Locale.ROOT));
                statement.setString(3, passwordHash);
                statement.setString(4, fullName.trim());
                statement.setString(5, phone.trim());
                statement.setString(6, role.name());
                statement.setString(7, status.name());
                long id;
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) throw new SQLException("User record was not created.");
                    id = result.getLong(1);
                }
                return id;
            } catch (SQLException exception) {
                throw exception;
            }
        }
    }

    public boolean recordFailedLogin(long userId, int maximumAttempts, int lockoutMinutes) throws SQLException {
        String sql = "UPDATE users SET failed_login_attempts = failed_login_attempts + 1, "
                + "status = CASE WHEN failed_login_attempts + 1 >= ? THEN 'LOCKED' ELSE status END, "
                + "locked_until = CASE WHEN failed_login_attempts + 1 >= ? "
                + "THEN CURRENT_TIMESTAMP + (? * INTERVAL '1 minute') ELSE locked_until END, "
                + "updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status = 'ACTIVE' "
                + "RETURNING status";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, maximumAttempts);
            statement.setInt(2, maximumAttempts);
            statement.setInt(3, lockoutMinutes);
            statement.setLong(4, userId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() && UserStatus.LOCKED.name().equals(result.getString("status"));
            }
        }
    }

    public boolean restoreExpiredLock(long userId) throws SQLException {
        String sql = "UPDATE users SET status = 'ACTIVE', failed_login_attempts = 0, locked_until = NULL, "
                + "updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status = 'LOCKED' "
                + "AND locked_until IS NOT NULL AND locked_until <= CURRENT_TIMESTAMP";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean recordSuccessfulLogin(long userId) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP, failed_login_attempts = 0, "
                + "locked_until = NULL, updated_at = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean updateLastLogin(long userId) throws SQLException {
        return recordSuccessfulLogin(userId);
    }

    public void incrementFailedAttempts(long userId, int maximumAttempts, int lockoutMinutes) throws SQLException {
        recordFailedLogin(userId, maximumAttempts, lockoutMinutes);
    }

    public void resetFailedAttempts(long userId) throws SQLException {
        String sql = "UPDATE users SET failed_login_attempts = 0, locked_until = NULL, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    public void lockUser(long userId) throws SQLException {
        String sql = "UPDATE users SET status = 'LOCKED', locked_until = NULL, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    public boolean setAccountStatus(long userId, UserStatus status) throws SQLException {
        String sql = "UPDATE users SET status = ?, locked_until = NULL, failed_login_attempts = 0, "
                + "updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    /** Promote a reviewed registration request without accepting a caller-selected role. */
    public boolean approveRegistration(long userId, UserRole approvedRole) throws SQLException {
        if (approvedRole == null || approvedRole == UserRole.ADMIN) {
            throw new IllegalArgumentException("New registrations cannot be granted administrator access.");
        }
        String sql = "UPDATE users SET role = ?, status = 'ACTIVE', locked_until = NULL, "
                + "failed_login_attempts = 0, updated_at = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND status = 'PENDING'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, approvedRole.name());
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    private User map(ResultSet result) throws SQLException {
        Timestamp lockTimestamp = result.getTimestamp("locked_until");
        return new User(result.getLong("user_id"), result.getString("username"),
                result.getString("email"), result.getString("password_hash"),
                result.getString("full_name"), result.getString("phone"),
                UserRole.fromDatabase(result.getString("role")),
                UserStatus.valueOf(result.getString("status")), result.getInt("failed_login_attempts"),
                lockTimestamp == null ? null : lockTimestamp.toInstant());
    }

}
