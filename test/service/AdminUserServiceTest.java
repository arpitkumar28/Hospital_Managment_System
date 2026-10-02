package service;

import dao.UserDAO;
import dao.AuditDAO;
import model.StaffAccountSummary;
import model.AuthenticatedUser;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.Test;
import security.SessionManager;

import java.time.Instant;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserServiceTest {
    @Test void rejectsUnauthenticatedAndEveryNonAdminBeforeUserOrAuditDao() {
        for (UserRole role : UserRole.values()) {
            if (role == UserRole.ADMIN) continue;
            assertDenied(role);
        }
        SessionManager sessions = new SessionManager(java.time.Clock.systemUTC());
        FakeUsers users = new FakeUsers(); FakeAudit audit = new FakeAudit();
        AdminUserService service = new AdminUserService(users, audit, sessions);
        assertThrows(AdminUserService.AdminOperationException.class, service::listPendingUsers);
        assertEquals(0, users.calls); assertEquals(0, audit.calls);
    }

    @Test void allowsAdministratorOperationsAfterSessionCheck() throws Exception {
        SessionManager sessions = new SessionManager(java.time.Clock.systemUTC());
        sessions.createSession(user(UserRole.ADMIN));
        FakeUsers users = new FakeUsers(); FakeAudit audit = new FakeAudit();
        AdminUserService service = new AdminUserService(users, audit, sessions);
        assertNotNull(service.listPendingUsers());
        service.approve(9, UserRole.DOCTOR);
        service.reject(9);
        service.activate(9);
        service.deactivate(9);
        service.lock(9);
        service.unlock(9);
        service.changeRole(9, UserRole.ACCOUNTANT);
        service.resetPassword(9, "ValidPass9!".toCharArray());
        assertEquals(9, users.calls);
        assertEquals(9, audit.calls);
        assertEquals(UserStatus.LOCKED, users.updatedStatus);
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.approve(9, UserRole.ADMIN));
    }

    @Test void reportsAuditFailureAfterAnAdminAction() {
        SessionManager sessions = new SessionManager(java.time.Clock.systemUTC()); sessions.createSession(user(UserRole.ADMIN));
        FakeUsers users = new FakeUsers(); FakeAudit audit = new FakeAudit(); audit.result = false;
        AdminUserService service = new AdminUserService(users, audit, sessions);
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.activate(9));
        assertEquals(1, users.calls);
        assertEquals(1, audit.calls);
    }

    private static void assertDenied(UserRole role) {
        SessionManager sessions = new SessionManager(java.time.Clock.systemUTC()); sessions.createSession(user(role));
        FakeUsers users = new FakeUsers(); FakeAudit audit = new FakeAudit();
        AdminUserService service = new AdminUserService(users, audit, sessions);
        assertThrows(AdminUserService.AdminOperationException.class, service::listPendingUsers);
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.approve(9, UserRole.DOCTOR));
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.reject(9));
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.activate(9));
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.deactivate(9));
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.lock(9));
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.unlock(9));
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.changeRole(9, UserRole.DOCTOR));
        assertThrows(AdminUserService.AdminOperationException.class, () -> service.resetPassword(9, "ValidPass9!".toCharArray()));
        assertEquals(0, users.calls, role + " called UserDAO"); assertEquals(0, audit.calls, role + " called AuditDAO");
    }

    private static User user(UserRole role) {
        return new User(1, "user", "user@example.org", "$2a$hash", "Test User", "5550100",
                role, UserStatus.ACTIVE, 0, null);
    }

    static class FakeUsers extends UserDAO {
        int calls;
        UserStatus updatedStatus;
        @Override public boolean changeStatus(long id, UserStatus status, long actor) {
            calls++; updatedStatus = status; return true;
        }
        @Override public List<StaffAccountSummary> findPendingUsers() { calls++; return List.of(); }
        @Override public boolean approveRegistration(long id, UserRole role, long actor) { calls++; return true; }
        @Override public boolean rejectPendingRegistration(long id, long actor) { calls++; return true; }
        @Override public boolean unlockAccount(long id, long actor) { calls++; return true; }
        @Override public boolean updateRole(long id, UserRole role, long actor) { calls++; return true; }
        @Override public boolean resetPassword(long id, String hash, long actor) { calls++; return true; }
    }
    static class FakeAudit extends AuditDAO {
        int calls;
        boolean result = true;
        @Override public boolean recordEvent(Long id, String event) { calls++; return result; }
    }
}
