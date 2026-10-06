package service;

import dao.AuditDAO;
import dao.UserDAO;
import model.AuthenticatedUser;
import model.UserRole;
import model.UserStatus;
import model.StaffAccountSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.PasswordUtil;
import security.AuthorizationService;
import security.SessionManager;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/** ADMIN-only staff-account operations. Every method rechecks the current session. */
public class AdminUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserService.class);
    private final UserDAO users;
    private final AuditDAO audit;
    private final SessionManager sessions;
    private final AuthorizationService authorization;

    public AdminUserService() { this(new UserDAO(), new AuditDAO(), SessionManager.INSTANCE, new AuthorizationService()); }

    public AdminUserService(UserDAO users, AuditDAO audit, SessionManager sessions) {
        this(users, audit, sessions, new AuthorizationService());
    }

    public AdminUserService(UserDAO users, AuditDAO audit, SessionManager sessions,
                            AuthorizationService authorization) {
        this.users = users;
        this.audit = audit;
        this.sessions = sessions;
        this.authorization = authorization;
    }

    public List<StaffAccountSummary> listPendingUsers() throws AdminOperationException {
        AuthenticatedUser actor = requireAdmin();
        try {
            List<StaffAccountSummary> pending = users.findPendingUsers();
            requireAuditRecorded(actor.userId(), "PENDING_USERS_VIEWED");
            return pending;
        } catch (SQLException exception) { throw databaseError("Unable to load pending accounts.", exception); }
    }

    public List<StaffAccountSummary> listUsers() throws AdminOperationException {
        AuthenticatedUser actor = requireAdmin();
        try {
            List<StaffAccountSummary> accounts = users.findAllUsers();
            requireAuditRecorded(actor.userId(), "USER_LIST_VIEWED");
            return accounts;
        } catch (SQLException exception) { throw databaseError("Unable to load user accounts.", exception); }
    }

    public long createUser(String fullName, String username, String email, String phone, UserRole role,
                           char[] password, char[] confirmation) throws AdminOperationException {
        try {
            AuthenticatedUser actor = requireAdmin();
            if (role == null || role == UserRole.ADMIN) {
                throw new AdminOperationException("Choose Receptionist, Doctor, or Accountant.");
            }
            List<String> errors = ValidationUtil.validateRegistration(fullName, username, email, phone,
                    password, confirmation);
            if (!errors.isEmpty()) throw new AdminOperationException(String.join("\n", errors));
            String normalizedUsername = username.trim();
            String normalizedEmail = email.trim();
            if (users.existsByUsername(normalizedUsername)) {
                throw new AdminOperationException("That username is already in use.");
            }
            if (users.existsByEmail(normalizedEmail)) {
                throw new AdminOperationException("That email address is already registered.");
            }
            String hash = PasswordUtil.hashPassword(password);
            return users.createManagedUser(fullName.trim(), normalizedUsername, normalizedEmail,
                    phone.trim(), hash, role, actor.userId());
        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                throw new AdminOperationException("That username or email address is already registered.");
            }
            throw databaseError("Unable to create the account.", exception);
        } finally {
            if (password != null) Arrays.fill(password, '\0');
            if (confirmation != null) Arrays.fill(confirmation, '\0');
        }
    }

    public void approve(long userId, UserRole role) throws AdminOperationException {
        AuthenticatedUser actor = requireAdmin();
        if (role == null || role == UserRole.ADMIN) throw new AdminOperationException("Choose a permitted staff role.");
        try {
            if (!users.approveRegistration(userId, role, actor.userId())) throw new AdminOperationException("The pending account could not be approved.");
            record(actor, userId, "USER_APPROVED_" + role.name());
        } catch (SQLException exception) { throw databaseError("Unable to approve the account.", exception); }
    }

    public void reject(long userId) throws AdminOperationException {
        AuthenticatedUser actor = requireAdmin();
        try {
            if (!users.rejectPendingRegistration(userId, actor.userId())) {
                throw new AdminOperationException("Only a pending account can be rejected.");
            }
            record(actor, userId, "USER_REJECTED");
        } catch (SQLException exception) { throw databaseError("Unable to reject the account.", exception); }
    }
    public void activate(long userId) throws AdminOperationException { changeStatus(userId, UserStatus.ACTIVE, "USER_ACTIVATED"); }
    public void deactivate(long userId) throws AdminOperationException { changeStatus(userId, UserStatus.INACTIVE, "USER_DEACTIVATED"); }
    public void lock(long userId) throws AdminOperationException { changeStatus(userId, UserStatus.LOCKED, "USER_LOCKED_BY_ADMIN"); }
    public void unlock(long userId) throws AdminOperationException {
        AuthenticatedUser actor = requireAdmin();
        try {
            if (!users.unlockAccount(userId, actor.userId())) {
                throw new AdminOperationException("Only a locked account can be unlocked.");
            }
            record(actor, userId, "USER_UNLOCKED_BY_ADMIN");
        } catch (SQLException exception) { throw databaseError("Unable to unlock the account.", exception); }
    }

    public void changeRole(long userId, UserRole role) throws AdminOperationException {
        AuthenticatedUser actor = requireAdmin();
        if (role == null) throw new AdminOperationException("Choose a valid staff role.");
        try {
            if (!users.updateRole(userId, role, actor.userId())) {
                throw new AdminOperationException("The role could not be changed. Ensure another active administrator exists.");
            }
            record(actor, userId, "USER_ROLE_CHANGED_" + role.name());
        } catch (SQLException exception) { throw databaseError("Unable to change the account role.", exception); }
    }

    public void resetPassword(long userId, char[] newPassword) throws AdminOperationException {
        try {
            AuthenticatedUser actor = requireAdmin();
            if (!strong(newPassword)) throw new AdminOperationException("Use at least 8 characters with uppercase, lowercase, a number and a symbol.");
            String hash = PasswordUtil.hashPassword(newPassword);
            if (!users.resetPassword(userId, hash, actor.userId())) throw new AdminOperationException("The account was not found.");
            record(actor, userId, "USER_PASSWORD_RESET");
        } catch (SQLException exception) { throw databaseError("Unable to reset the account password.", exception); }
        finally { if (newPassword != null) Arrays.fill(newPassword, '\0'); }
    }

    private void changeStatus(long userId, UserStatus status, String event) throws AdminOperationException {
        AuthenticatedUser actor = requireAdmin();
        if (actor.userId() == userId && actor.role() == UserRole.ADMIN
                && (status == UserStatus.INACTIVE || status == UserStatus.LOCKED)) {
            throw new AdminOperationException("You cannot deactivate or lock your own active administrator account.");
        }
        try {
            if (!users.changeStatus(userId, status, actor.userId())) {
                throw new AdminOperationException("The account status could not be changed. Ensure another active administrator exists.");
            }
            record(actor, userId, event);
        } catch (SQLException exception) { throw databaseError("Unable to update the account status.", exception); }
    }

    private AuthenticatedUser requireAdmin() throws AdminOperationException {
        AuthenticatedUser actor = sessions.getCurrentUser().orElse(null);
        try { authorization.requireModule(actor, AuthorizationService.USER_MANAGEMENT); }
        catch (AuthorizationService.AccessDeniedException exception) {
            throw new AdminOperationException("Administrator access is required for this operation.");
        }
        return actor;
    }

    private void record(AuthenticatedUser actor, long subjectId, String event) throws AdminOperationException {
        requireAuditRecorded(actor.userId(), event + ":" + subjectId);
    }

    private void requireAuditRecorded(long actorId, String event) throws AdminOperationException {
        if (!audit.recordEvent(actorId, event)) {
            throw new AdminOperationException("Unable to record the account-management audit event.");
        }
    }

    private AdminOperationException databaseError(String message, SQLException exception) {
        LOGGER.error(message, exception);
        return new AdminOperationException(message);
    }

    private boolean strong(char[] password) {
        if (password == null || password.length < 8) return false;
        boolean upper = false, lower = false, digit = false, special = false;
        for (char c : password) {
            upper |= Character.isUpperCase(c); lower |= Character.isLowerCase(c);
            digit |= Character.isDigit(c); special |= !Character.isLetterOrDigit(c);
        }
        return upper && lower && digit && special;
    }

    public static class AdminOperationException extends Exception {
        public AdminOperationException(String message) { super(message); }
    }
}
