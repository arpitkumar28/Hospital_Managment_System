package dao;

import database.DatabaseConnection;
import model.User;
import model.UserRole;
import model.UserStatus;
import model.StaffAccountSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
        return findByIdentifier("SELECT user_id, username, email, password_hash, full_name, phone, role, status, "
                + "failed_login_attempts, locked_until FROM users WHERE lower(username) = lower(?)", username);
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        return findByIdentifier("SELECT user_id, username, email, password_hash, full_name, phone, role, status, "
                + "failed_login_attempts, locked_until FROM users WHERE lower(email) = lower(?)", email);
    }

    private Optional<User> findByIdentifier(String sql, String value) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE lower(username) = lower(?) LIMIT 1";
        return exists(sql, username);
    }

    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE lower(email) = lower(?) LIMIT 1";
        return exists(sql, email);
    }

    private boolean exists(String sql, String value) throws SQLException {
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
            connection.setAutoCommit(false);
            try {
                long id;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, username.trim());
                    statement.setString(2, email.trim().toLowerCase(java.util.Locale.ROOT));
                    statement.setString(3, passwordHash);
                    statement.setString(4, fullName.trim());
                    statement.setString(5, phone.trim());
                    statement.setString(6, role.name());
                    statement.setString(7, status.name());
                    try (ResultSet result = statement.executeQuery()) {
                        if (!result.next()) throw new SQLException("User record was not created.");
                        id = result.getLong(1);
                    }
                }
                try (PreparedStatement event = connection.prepareStatement(
                        "INSERT INTO audit_logs (user_id, event_type) VALUES (?, 'USER_REGISTERED')")) {
                    event.setLong(1, id);
                    event.executeUpdate();
                }
                connection.commit();
                return id;
            } catch (SQLException exception) {
                try { connection.rollback(); } catch (SQLException rollback) { exception.addSuppressed(rollback); }
                throw exception;
            } finally {
                try { connection.setAutoCommit(true); } catch (SQLException exception) {
                    LOGGER.warn("Unable to reset registration connection state.", exception);
                }
            }
        }
    }

    public boolean hasAdministrator() throws SQLException {
        String sql = "SELECT 1 FROM users WHERE role = 'ADMIN' LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            return result.next();
        }
    }

    /** Creates the first administrator under a transaction-scoped PostgreSQL advisory lock. */
    public boolean createInitialAdministrator(String username, String email, String fullName,
                                              String phone, String passwordHash) throws SQLException {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, phone, role, status) "
                + "VALUES (?, ?, ?, ?, ?, 'ADMIN', 'ACTIVE') RETURNING user_id";
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement lock = connection.prepareStatement("SELECT pg_advisory_xact_lock(?)")) {
                    lock.setLong(1, 591234876L);
                    lock.execute();
                }
                try (PreparedStatement exists = connection.prepareStatement(
                        "SELECT 1 FROM users WHERE role = 'ADMIN' LIMIT 1");
                     ResultSet result = exists.executeQuery()) {
                    if (result.next()) {
                        connection.rollback();
                        return false;
                    }
                }
                long id;
                try (PreparedStatement insert = connection.prepareStatement(sql)) {
                    insert.setString(1, username.trim());
                    insert.setString(2, email.trim().toLowerCase(java.util.Locale.ROOT));
                    insert.setString(3, passwordHash);
                    insert.setString(4, fullName.trim());
                    insert.setString(5, phone == null ? "" : phone.trim());
                    try (ResultSet result = insert.executeQuery()) {
                        if (!result.next()) throw new SQLException("Initial administrator was not created.");
                        id = result.getLong(1);
                    }
                }
                try (PreparedStatement event = connection.prepareStatement(
                        "INSERT INTO audit_logs (user_id, event_type) VALUES (?, 'INITIAL_ADMIN_CREATED')")) {
                    event.setLong(1, id);
                    event.executeUpdate();
                }
                connection.commit();
                return true;
            } catch (SQLException exception) {
                try { connection.rollback(); } catch (SQLException rollback) { exception.addSuppressed(rollback); }
                throw exception;
            } finally {
                try { connection.setAutoCommit(true); } catch (SQLException exception) {
                    LOGGER.warn("Unable to reset administrator provisioning connection state.", exception);
                }
            }
        }
    }

    public List<StaffAccountSummary> findPendingUsers() throws SQLException {
        String sql = "SELECT user_id, username, email, full_name, phone, role, status, created_at, last_login "
                + "FROM users WHERE status = 'PENDING' ORDER BY created_at, user_id";
        List<StaffAccountSummary> pending = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) pending.add(new StaffAccountSummary(result.getLong("user_id"),
                    result.getString("username"), result.getString("email"), result.getString("full_name"),
                    result.getString("phone"), UserRole.fromDatabase(result.getString("role")),
                    UserStatus.valueOf(result.getString("status")), result.getTimestamp("created_at").toInstant(),
                    result.getTimestamp("last_login") == null ? null : result.getTimestamp("last_login").toInstant()));
        }
        return List.copyOf(pending);
    }

    public boolean updateRole(long userId, UserRole role, long actorId) throws SQLException {
        String sql = "UPDATE users SET role = ?, updated_by = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND NOT (role = 'ADMIN' AND ? <> 'ADMIN' AND NOT EXISTS "
                + "(SELECT 1 FROM users another WHERE another.role = 'ADMIN' AND another.status = 'ACTIVE' "
                + "AND another.user_id <> users.user_id))";
        return updateAdminControlledRecord(sql, userId, actorId, role.name(), null);
    }

    public boolean changeStatus(long userId, UserStatus status, long actorId) throws SQLException {
        String sql = "UPDATE users SET status = ?, locked_until = NULL, failed_login_attempts = 0, "
                + "updated_by = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? "
                + "AND ((? = 'ACTIVE' AND status IN ('PENDING', 'INACTIVE')) "
                + "OR (? = 'INACTIVE' AND status <> 'INACTIVE') "
                + "OR (? = 'LOCKED' AND status = 'ACTIVE')) "
                + "AND NOT (role = 'ADMIN' AND status = 'ACTIVE' AND ? <> 'ACTIVE' AND NOT EXISTS "
                + "(SELECT 1 FROM users another WHERE another.role = 'ADMIN' AND another.status = 'ACTIVE' "
                + "AND another.user_id <> users.user_id))";
        return updateAdminControlledRecord(sql, userId, actorId, status.name(), status);
    }

    private boolean updateAdminControlledRecord(String sql, long userId, long actorId,
                                                String roleOrStatus, UserStatus status) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement lock = connection.prepareStatement("SELECT pg_advisory_xact_lock(?)")) {
                    lock.setLong(1, 591234877L);
                    lock.execute();
                }
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    if (status == null) {
                        statement.setString(1, roleOrStatus);
                        statement.setLong(2, actorId);
                        statement.setLong(3, userId);
                        statement.setString(4, roleOrStatus);
                    } else {
                        statement.setString(1, status.name());
                        statement.setLong(2, actorId);
                        statement.setLong(3, userId);
                        statement.setString(4, status.name());
                        statement.setString(5, status.name());
                        statement.setString(6, status.name());
                        statement.setString(7, status.name());
                    }
                    boolean changed = statement.executeUpdate() == 1;
                    connection.commit();
                    return changed;
                }
            } catch (SQLException exception) {
                try { connection.rollback(); } catch (SQLException rollback) { exception.addSuppressed(rollback); }
                throw exception;
            } finally {
                try { connection.setAutoCommit(true); } catch (SQLException exception) {
                    LOGGER.warn("Unable to reset account update connection state.", exception);
                }
            }
        }
    }

    public boolean rejectPendingRegistration(long userId, long actorId) throws SQLException {
        String sql = "UPDATE users SET status = 'INACTIVE', updated_by = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND status = 'PENDING'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, actorId);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean unlockAccount(long userId, long actorId) throws SQLException {
        String sql = "UPDATE users SET status = 'ACTIVE', locked_until = NULL, failed_login_attempts = 0, "
                + "updated_by = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND status = 'LOCKED'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, actorId);
            statement.setLong(2, userId);
            return statement.executeUpdate() == 1;
        }
    }

    public boolean resetPassword(long userId, String passwordHash, long actorId) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, status = CASE WHEN status = 'LOCKED' THEN 'ACTIVE' ELSE status END, "
                + "failed_login_attempts = 0, locked_until = NULL, updated_by = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, passwordHash);
            statement.setLong(2, actorId);
            statement.setLong(3, userId);
            return statement.executeUpdate() == 1;
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
    public boolean approveRegistration(long userId, UserRole approvedRole, long actorId) throws SQLException {
        if (approvedRole == null || approvedRole == UserRole.ADMIN) {
            throw new IllegalArgumentException("New registrations cannot be granted administrator access.");
        }
        String sql = "UPDATE users SET role = ?, status = 'ACTIVE', locked_until = NULL, "
                + "failed_login_attempts = 0, updated_by = ?, updated_at = CURRENT_TIMESTAMP "
                + "WHERE user_id = ? AND status = 'PENDING'";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, approvedRole.name());
            statement.setLong(2, actorId);
            statement.setLong(3, userId);
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
