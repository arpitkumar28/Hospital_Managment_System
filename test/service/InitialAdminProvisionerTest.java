package service;

import dao.UserDAO;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InitialAdminProvisionerTest {
    @Test void createsOnlyOnceAndRequiresAllEnvironmentValues() throws Exception {
        FakeUsers users = new FakeUsers();
        InitialAdminProvisioner provisioner = new InitialAdminProvisioner(users);
        Map<String, String> environment = Map.of(
                "HOSPITAL_INITIAL_ADMIN_USERNAME", "initial_admin",
                "HOSPITAL_INITIAL_ADMIN_EMAIL", "admin@example.org",
                "HOSPITAL_INITIAL_ADMIN_PASSWORD", "HealthyPass9!");
        assertTrue(provisioner.provisionIfConfigured(environment));
        assertFalse(provisioner.provisionIfConfigured(environment));
        assertEquals(1, users.created);
        assertFalse(users.plaintextStored);
        assertFalse(provisioner.provisionIfConfigured(Map.of()));
        assertThrows(InitialAdminProvisioner.ProvisioningException.class,
                () -> provisioner.provisionIfConfigured(Map.of("HOSPITAL_INITIAL_ADMIN_USERNAME", "admin")));
    }

    static class FakeUsers extends UserDAO {
        int created;
        boolean plaintextStored;
        boolean exists;
        @Override public boolean createInitialAdministrator(String username, String email, String fullName,
                                                            String phone, String passwordHash) throws SQLException {
            if (exists) return false;
            exists = true; created++;
            plaintextStored = !passwordHash.startsWith("$2");
            return true;
        }
    }
}
