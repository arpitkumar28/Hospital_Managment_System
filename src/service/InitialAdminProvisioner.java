package service;

import dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.PasswordUtil;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

/** Explicit first-run setup. Configure all three HOSPITAL_INITIAL_ADMIN_* variables to run. */
public class InitialAdminProvisioner {
    private static final Logger LOGGER = LoggerFactory.getLogger(InitialAdminProvisioner.class);
    private static final String USERNAME = "HOSPITAL_INITIAL_ADMIN_USERNAME";
    private static final String EMAIL = "HOSPITAL_INITIAL_ADMIN_EMAIL";
    private static final String PASSWORD = "HOSPITAL_INITIAL_ADMIN_PASSWORD";

    private final UserDAO users;

    public InitialAdminProvisioner() { this(new UserDAO()); }
    public InitialAdminProvisioner(UserDAO users) { this.users = users; }

    /** Returns false when setup variables are absent or an administrator already exists. */
    public boolean provisionIfConfigured() throws ProvisioningException {
        return provisionIfConfigured(System.getenv());
    }

    boolean provisionIfConfigured(Map<String, String> environment) throws ProvisioningException {
        String username = environment.get(USERNAME);
        String email = environment.get(EMAIL);
        String passwordText = environment.get(PASSWORD);
        boolean any = nonblank(username) || nonblank(email) || nonblank(passwordText);
        if (!any) return false;
        if (!nonblank(username) || !nonblank(email) || !nonblank(passwordText)) {
            throw new ProvisioningException("Set all three initial administrator environment variables to run setup.");
        }
        char[] password = passwordText.toCharArray();
        try {
            if (!ValidationUtil.validateInitialAdministrator(username, email, password).isEmpty())
                throw new ProvisioningException("Initial administrator credentials do not meet account requirements.");
            String hash = PasswordUtil.hashPassword(password);
            try {
                boolean created = users.createInitialAdministrator(username.trim(), email.trim(),
                        username.trim(), "", hash);
                if (created) LOGGER.info("Initial administrator account provisioned.");
                else LOGGER.info("Initial administrator setup skipped because an administrator already exists.");
                return created;
            } catch (SQLException exception) {
                LOGGER.error("Initial administrator provisioning failed.", exception);
                throw new ProvisioningException("Unable to provision the initial administrator. Check the database connection and schema.");
            }
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private boolean nonblank(String value) { return value != null && !value.isBlank(); }

    public static void main(String[] args) {
        try {
            boolean created = new InitialAdminProvisioner().provisionIfConfigured();
            if (!created) System.out.println("No account was created. Configure all initial administrator variables and rerun.");
        } catch (ProvisioningException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    public static class ProvisioningException extends Exception {
        public ProvisioningException(String message) { super(message); }
    }
}
