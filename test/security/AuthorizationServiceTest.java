package security;

import model.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTest {
    private final AuthorizationService policy = new AuthorizationService();

    @Test void grantsOnlyConfiguredModuleRoles() {
        assertTrue(policy.canAccessModule(UserRole.RECEPTIONIST, "Patients"));
        assertFalse(policy.canAccessModule(UserRole.DOCTOR, "Billing"));
        assertFalse(policy.canAccessModule(UserRole.ADMIN, "Unlisted module"));
    }
}
