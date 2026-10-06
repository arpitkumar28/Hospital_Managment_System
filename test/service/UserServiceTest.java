package service;

import dao.AuditDAO;
import dao.UserDAO;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    @Test void registersValidatedUsersAsReceptionistPending() throws Exception {
        FakeUsers users = new FakeUsers();
        UserService service = new UserService(users);
        service.register("A Staff Member", "staff_1", "staff@example.org", "5550100",
                "HealthyPass9!".toCharArray(), "HealthyPass9!".toCharArray());
        assertEquals(UserRole.RECEPTIONIST, users.role);
        assertEquals(UserStatus.PENDING, users.status);
    }

    @Test void rejectsDuplicateUsernameAndEmail() {
        FakeUsers users = new FakeUsers();
        users.existing.add("username:staff_1");
        UserService service = new UserService(users);
        assertThrows(UserService.RegistrationException.class, () -> service.register("Staff Member", "staff_1",
                "staff@example.org", "5550100", "HealthyPass9!".toCharArray(), "HealthyPass9!".toCharArray()));
        users.existing.clear();
        users.existing.add("email:staff@example.org");
        assertThrows(UserService.RegistrationException.class, () -> service.register("Staff Member", "staff_1",
                "staff@example.org", "5550100", "HealthyPass9!".toCharArray(), "HealthyPass9!".toCharArray()));
    }

    static class FakeUsers extends UserDAO {
        final Set<String> existing = new HashSet<>();
        UserRole role;
        UserStatus status;
        @Override public boolean existsByUsername(String value) { return existing.contains("username:" + value); }
        @Override public boolean existsByEmail(String value) { return existing.contains("email:" + value); }
        @Override public long createUser(String fullName, String username, String email, String phone,
                                         String hash, UserRole role, UserStatus status) {
            this.role = role; this.status = status; return 11;
        }
    }
}
