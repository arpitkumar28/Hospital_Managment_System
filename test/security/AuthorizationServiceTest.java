package security;

import model.UserRole;
import model.AuthenticatedUser;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTest {
    private final AuthorizationService policy = new AuthorizationService();

    @Test void grantsOnlyConfiguredModuleRoles() {
        assertTrue(policy.canAccessModule(UserRole.RECEPTIONIST, "Patients"));
        assertFalse(policy.canAccessModule(UserRole.DOCTOR, "Billing"));
        assertFalse(policy.canAccessModule(UserRole.ADMIN, "Unlisted module"));
    }

    @Test void roleMatrixMatchesServiceBoundaries() {
        assertRoles("Dashboard", UserRole.values());
        assertRoles("Patients", UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.DOCTOR);
        assertRoles("Doctors", UserRole.ADMIN, UserRole.RECEPTIONIST);
        assertRoles("Appointments", UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.DOCTOR);
        assertRoles("Admissions", UserRole.ADMIN, UserRole.RECEPTIONIST);
        assertRoles("Rooms", UserRole.ADMIN, UserRole.RECEPTIONIST);
        assertRoles("Beds", UserRole.ADMIN, UserRole.RECEPTIONIST);
        assertRoles("Billing", UserRole.ADMIN, UserRole.ACCOUNTANT);
        assertRoles("Reports", UserRole.ADMIN, UserRole.ACCOUNTANT);
        assertRoles("User Management", UserRole.ADMIN);
    }

    @Test void rejectsMissingSessionAtEveryServiceGate() {
        for (String module : new String[]{"Dashboard", "Patients", "Doctors", "Appointments", "Admissions",
                "Rooms", "Beds", "Billing", "Reports", "User Management"}) {
            assertThrows(AuthorizationService.AccessDeniedException.class, () -> policy.requireModule(null, module));
        }
        assertThrows(AuthorizationService.AccessDeniedException.class, () -> policy.requireAnyRole(null, UserRole.ADMIN));
    }

    private void assertRoles(String module, UserRole... allowed) {
        for (UserRole role : UserRole.values()) {
            boolean expected = java.util.Arrays.asList(allowed).contains(role);
            assertEquals(expected, policy.canAccessModule(role, module), module + " / " + role);
        }
    }
}
