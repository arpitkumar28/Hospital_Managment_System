package service;

import dao.ReportDAO;
import model.BillingReport;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.Test;
import security.AuthorizationService;
import security.SessionManager;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {
    private static final LocalDate FROM = LocalDate.of(2026, 1, 1);
    private static final LocalDate TO = LocalDate.of(2026, 1, 31);

    @Test
    void onlyAuthorizedStaffCanReachReportDao() {
        for (UserRole role : UserRole.values()) {
            SessionManager sessions = new SessionManager(Clock.systemUTC());
            sessions.createSession(user(role));
            FakeReportDAO dao = new FakeReportDAO();
            ReportService service = new ReportService(dao, sessions, new AuthorizationService());
            if (role == UserRole.ADMIN || role == UserRole.ACCOUNTANT) {
                assertEquals(3, service.getBillingReport(FROM, TO).billCount());
                assertEquals(1, dao.calls);
            } else {
                assertThrows(AuthorizationService.AccessDeniedException.class,
                        () -> service.getBillingReport(FROM, TO));
                assertEquals(0, dao.calls);
            }
        }
    }

    @Test
    void unauthenticatedOrInvalidRangeIsRejectedBeforeDaoAccess() {
        FakeReportDAO dao = new FakeReportDAO();
        ReportService service = new ReportService(dao, new SessionManager(Clock.systemUTC()),
                new AuthorizationService());
        assertThrows(IllegalArgumentException.class, () -> service.getBillingReport(FROM, null));
        assertThrows(IllegalArgumentException.class, () -> service.getBillingReport(TO, FROM));
        assertThrows(AuthorizationService.AccessDeniedException.class,
                () -> service.getBillingReport(FROM, TO));
        assertEquals(0, dao.calls);
    }

    private static User user(UserRole role) {
        return new User(1, "staff", "staff@example.org", "hash", "Reports Staff", "",
                role, UserStatus.ACTIVE, 0, null);
    }

    private static final class FakeReportDAO extends ReportDAO {
        private int calls;

        @Override public BillingReport getBillingReport(LocalDate from, LocalDate to) {
            calls++;
            return new BillingReport(from, to, 3, new BigDecimal("100.00"),
                    new BigDecimal("40.00"), new BigDecimal("60.00"), List.of(), List.of(), List.of());
        }
    }
}
