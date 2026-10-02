package security;

import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    @Test void storesOnlyIdentityUntilLogout() {
        SessionManager sessions = new SessionManager(Clock.fixed(Instant.EPOCH, ZoneOffset.UTC));
        User user = new User(4, "staff", "staff@example.org", "hash", "Staff Person", "5550100",
                UserRole.DOCTOR, UserStatus.ACTIVE, 0, null);
        assertEquals(Instant.EPOCH, sessions.createSession(user).loginTime());
        assertTrue(sessions.isLoggedIn());
        assertEquals(4, sessions.logout().orElseThrow().userId());
        assertFalse(sessions.isLoggedIn());
    }
}
