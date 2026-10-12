package service;

import dao.AuditDAO;
import dao.PatientAuthDAO;
import model.PatientIdentity;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import security.PasswordUtil;
import security.PatientSession;
import security.SessionManager;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PatientAuthServiceTest {
    private final PatientSession sessions = new PatientSession(Clock.systemUTC());
    private final FakePatients patients = new FakePatients();
    private final FakeAudit audit = new FakeAudit();
    private final PatientAuthService service = new PatientAuthService(patients, sessions, audit);

    @BeforeEach
    void reset() {
        sessions.logout();
        PatientSession.INSTANCE.logout();
        SessionManager.INSTANCE.logout();
        patients.status = "ACTIVE";
        patients.lookupCalls = 0;
        patients.successCalls = 0;
        audit.events.clear();
        audit.result = true;
    }

    @Test
    void successfulLoginCreatesOnlyPatientIdentityAndMapsPatientRecord() throws Exception {
        char[] password = "HealthyPass9!".toCharArray();

        PatientIdentity identity = service.authenticate("patient", password);

        assertEquals(42, identity.patientId());
        assertEquals("Patient Name", identity.fullName());
        assertTrue(sessions.isLoggedIn());
        assertFalse(SessionManager.INSTANCE.isLoggedIn());
        assertEquals(1, patients.successCalls);
        assertTrue(audit.events.contains("PATIENT_LOGIN_SUCCESS"));
        assertArrayEquals(new char[password.length], password);
    }

    @Test
    void staffSessionBlocksPatientLookupBeforeDaoAccess() {
        SessionManager.INSTANCE.createSession(new User(7, "staff", "staff@example.org", "hash",
                "Staff Name", "5550100", UserRole.ADMIN, UserStatus.ACTIVE, 0, null));

        assertThrows(PatientAuthService.AuthException.class,
                () -> service.authenticate("patient", "HealthyPass9!".toCharArray()));

        assertEquals(0, patients.lookupCalls);
        assertFalse(sessions.isLoggedIn());
    }

    @Test
    void inactivePatientCannotCreatePatientSession() {
        patients.status = "PENDING";

        assertThrows(PatientAuthService.AuthException.class,
                () -> service.authenticate("patient", "HealthyPass9!".toCharArray()));

        assertFalse(sessions.isLoggedIn());
        assertEquals(0, patients.successCalls);
    }

    @Test
    void failedSuccessAuditPreventsPatientSessionCreation() {
        audit.result = false;

        assertThrows(PatientAuthService.AuthException.class,
                () -> service.authenticate("patient", "HealthyPass9!".toCharArray()));

        assertFalse(sessions.isLoggedIn());
    }

    private static final class FakePatients extends PatientAuthDAO {
        private final String hash = PasswordUtil.hashPassword("HealthyPass9!".toCharArray());
        private String status = "ACTIVE";
        private int lookupCalls;
        private int successCalls;

        @Override
        public Optional<Credential> findByIdentifier(String ignored) {
            lookupCalls++;
            return Optional.of(new Credential(42, "Patient Name", hash, status, 0, null));
        }

        @Override
        public boolean restoreExpiredLock(long patientId) {
            return false;
        }

        @Override
        public boolean recordSuccess(long patientId) {
            successCalls++;
            return "ACTIVE".equals(status);
        }
    }

    private static final class FakeAudit extends AuditDAO {
        private final List<String> events = new ArrayList<>();
        private boolean result = true;

        @Override
        public boolean recordEvent(Long userId, String eventType) {
            events.add(eventType);
            return result;
        }
    }
}
