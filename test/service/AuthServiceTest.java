package service;

import dao.AuditDAO;
import dao.UserDAO;
import model.AuthenticatedUser;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import security.PasswordUtil;
import security.SessionManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private final FakeUsers users = new FakeUsers();
    private final FakeAudit audit = new FakeAudit();
    private final SessionManager sessions = new SessionManager(java.time.Clock.systemUTC());
    private final AuthService service = new AuthService(users, audit, sessions);

    @BeforeEach void reset() {
        users.status = UserStatus.ACTIVE;
        users.failed = 0;
        audit.events.clear();
        sessions.logout();
    }

    @Test void successfulLoginCreatesSessionResetsCounterAndAudits() throws Exception {
        users.failed = 2;
        AuthenticatedUser authenticated = service.authenticate("staff", "HealthyPass9!".toCharArray());
        assertEquals(UserRole.RECEPTIONIST, authenticated.role());
        assertEquals(0, users.failed);
        assertTrue(sessions.isLoggedIn());
        assertTrue(audit.events.contains("LOGIN_SUCCESS"));
    }

    @Test void unknownLoginIsGenericAndAudited() {
        users.present = false;
        AuthService.AuthException error = assertThrows(AuthService.AuthException.class,
                () -> service.authenticate("nobody", "HealthyPass9!".toCharArray()));
        assertEquals("Your username or password is incorrect.", error.getMessage());
        assertTrue(audit.events.contains("LOGIN_FAILED"));
        assertFalse(sessions.isLoggedIn());
    }

    @Test void incorrectPasswordIncrementsCounter() {
        assertThrows(AuthService.AuthException.class,
                () -> service.authenticate("staff", "WrongPass9!".toCharArray()));
        assertEquals(1, users.failed);
        assertTrue(audit.events.contains("LOGIN_FAILED"));
    }

    @Test void thresholdLocksAccountAndAuditsLock() {
        users.failed = 4;
        AuthService.AuthException error = assertThrows(AuthService.AuthException.class,
                () -> service.authenticate("staff", "WrongPass9!".toCharArray()));
        assertEquals(UserStatus.LOCKED, users.status);
        assertEquals("Your username or password is incorrect.", error.getMessage());
        assertTrue(audit.events.contains("LOGIN_FAILED"));
        assertTrue(audit.events.contains("ACCOUNT_LOCKED"));
    }

    @Test void lockedPendingAndInactiveAccountsCannotCreateSessions() {
        for (UserStatus status : List.of(UserStatus.LOCKED, UserStatus.PENDING, UserStatus.INACTIVE)) {
            users.status = status;
            AuthService.AuthException error = assertThrows(AuthService.AuthException.class,
                    () -> service.authenticate("staff", "HealthyPass9!".toCharArray()));
            assertEquals("Your username or password is incorrect.", error.getMessage());
            assertFalse(sessions.isLoggedIn());
        }
    }

    static class FakeUsers extends UserDAO {
        String hash = PasswordUtil.hashPassword("HealthyPass9!".toCharArray());
        UserStatus status = UserStatus.ACTIVE;
        int failed;
        boolean present = true;

        @Override public Optional<User> findByUsernameOrEmail(String ignored) {
            if (!present) return Optional.empty();
            return Optional.of(new User(7, "staff", "staff@example.org", hash, "Staff Name", "5550100",
                    UserRole.RECEPTIONIST, status, failed, null));
        }
        @Override public boolean restoreExpiredLock(long id) { return false; }
        @Override public boolean recordFailedLogin(long id, int maximum, int lockout) {
            failed++;
            if (failed >= maximum) { status = UserStatus.LOCKED; return true; }
            return false;
        }
        @Override public boolean recordSuccessfulLogin(long id) {
            if (status != UserStatus.ACTIVE) return false;
            failed = 0;
            return true;
        }
    }

    static class FakeAudit extends AuditDAO {
        final List<String> events = new ArrayList<>();
        @Override public boolean recordEvent(Long userId, String event) { events.add(event); return true; }
    }
}
