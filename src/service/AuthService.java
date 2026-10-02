package service;

import config.AuthSecurityConfig;
import dao.AuditDAO;
import dao.UserDAO;
import model.AuthenticatedUser;
import model.User;
import model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.PasswordUtil;
import security.SessionManager;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

/** Authentication orchestration. Raw credentials never enter logs or session state. */
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final UserDAO users;
    private final AuditDAO audit;
    private final SessionManager sessions;

    public AuthService() { this(new UserDAO(), new AuditDAO(), SessionManager.INSTANCE); }

    public AuthService(UserDAO users, AuditDAO audit, SessionManager sessions) {
        this.users = users;
        this.audit = audit;
        this.sessions = sessions;
    }

    public AuthenticatedUser authenticate(String usernameOrEmail, char[] password) throws AuthException {
        char[] supplied = password == null ? new char[0] : password;
        try {
            if (sessions.isLoggedIn()) throw new AuthException("Sign out before starting another session.");
            if (!ValidationUtil.isValidLogin(usernameOrEmail, supplied)) {
                throw new AuthException("Enter your username or email and password.");
            }
            Optional<User> match = users.findByUsernameOrEmail(usernameOrEmail.trim());
            if (match.isEmpty()) {
                audit.recordEvent(null, "LOGIN_FAILED");
                throw new AuthException("Invalid username or password.");
            }
            User user = match.get();
            if (user.status() != UserStatus.ACTIVE) {
                if (user.status() == UserStatus.LOCKED && users.restoreExpiredLock(user.userId())) {
                    user = users.findByUsernameOrEmail(usernameOrEmail.trim()).orElse(user);
                }
            }
            if (user.status() != UserStatus.ACTIVE) {
                audit.recordEvent(user.userId(), "LOGIN_BLOCKED_" + user.status().name());
                throw new AuthException("This account is not active. Contact an administrator.");
            }
            if (!PasswordUtil.verifyPassword(supplied, user.passwordHash())) {
                boolean locked = users.recordFailedLogin(user.userId(),
                        AuthSecurityConfig.MAX_FAILED_ATTEMPTS, AuthSecurityConfig.LOCKOUT_MINUTES);
                audit.recordEvent(user.userId(), locked ? "ACCOUNT_LOCKED" : "LOGIN_FAILED");
                throw new AuthException(locked
                        ? "This account has been locked after repeated failed attempts. Contact an administrator."
                        : "Invalid username or password.");
            }
            if (!users.recordSuccessfulLogin(user.userId())) {
                throw new AuthException("This account is not active. Contact an administrator.");
            }
            AuthenticatedUser authenticated = sessions.createSession(user);
            audit.recordEvent(user.userId(), "LOGIN_SUCCEEDED");
            return authenticated;
        } catch (SQLException exception) {
            LOGGER.error("Authentication operation failed.", exception);
            throw new AuthException("Unable to sign in right now. Check the database connection and try again.");
        } finally {
            Arrays.fill(supplied, '\0');
        }
    }

    public void logout() {
        sessions.logout().ifPresent(user -> audit.recordEvent(user.userId(), "LOGOUT"));
    }

    public static class AuthException extends Exception {
        public AuthException(String message) { super(message); }
    }
}
