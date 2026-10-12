package service;

import dao.BillDAO;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.Test;
import security.AuthorizationService;
import security.SessionManager;

import java.math.BigDecimal;
import java.time.Clock;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {
    @Test
    void onlyAdminAndAccountantCanReachBillingDao() {
        for (UserRole role : UserRole.values()) {
            SessionManager sessions = new SessionManager(Clock.systemUTC());
            sessions.createSession(user(role));
            FakeBills bills = new FakeBills();
            BillingService service = new BillingService(bills, sessions, new AuthorizationService());

            if (role == UserRole.ADMIN || role == UserRole.ACCOUNTANT) {
                assertTrue(service.addPayment(10, BigDecimal.ONE));
                assertEquals(1, bills.calls);
            } else {
                assertThrows(AuthorizationService.AccessDeniedException.class,
                        () -> service.addPayment(10, BigDecimal.ONE));
                assertEquals(0, bills.calls, role + " must be denied before DAO access");
            }
        }

        SessionManager unauthenticated = new SessionManager(Clock.systemUTC());
        FakeBills bills = new FakeBills();
        BillingService service = new BillingService(bills, unauthenticated, new AuthorizationService());
        assertThrows(AuthorizationService.AccessDeniedException.class,
                () -> service.addPayment(10, BigDecimal.ONE));
        assertEquals(0, bills.calls);
    }

    @Test
    void missingBillsAndFailedDaoOperationsAreReturnedToCaller() {
        SessionManager sessions = new SessionManager(Clock.systemUTC());
        sessions.createSession(user(UserRole.ACCOUNTANT));
        FakeBills bills = new FakeBills();
        bills.paymentResult = false;
        bills.deleteResult = false;
        BillingService service = new BillingService(bills, sessions, new AuthorizationService());

        assertNull(service.getBillById(999));
        assertFalse(service.addPayment(999, BigDecimal.ONE));
        assertFalse(service.deleteBill(999));
        assertEquals(3, bills.calls);
    }

    @Test
    void databaseOperationErrorsAreNotConvertedToSuccess() {
        SessionManager sessions = new SessionManager(Clock.systemUTC());
        sessions.createSession(user(UserRole.ADMIN));
        FakeBills bills = new FakeBills();
        bills.failure = new IllegalStateException("database unavailable");
        BillingService service = new BillingService(bills, sessions, new AuthorizationService());

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.addPayment(10, BigDecimal.ONE));
        assertEquals("database unavailable", error.getMessage());
    }

    private static User user(UserRole role) {
        return new User(1, "staff", "staff@example.org", "hash", "Billing Staff", "",
                role, UserStatus.ACTIVE, 0, null);
    }

    private static final class FakeBills extends BillDAO {
        private int calls;
        private boolean paymentResult = true;
        private boolean deleteResult = true;
        private RuntimeException failure;

        @Override
        public boolean addPayment(int billId, BigDecimal amount) {
            calls++;
            if (failure != null) throw failure;
            return paymentResult;
        }

        @Override
        public boolean deleteBill(int billId) {
            calls++;
            return deleteResult;
        }

        @Override
        public Object[] getBillById(int billId) {
            calls++;
            return null;
        }
    }
}
