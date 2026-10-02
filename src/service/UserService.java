package service;

import dao.UserDAO;
import model.UserRole;
import model.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.PasswordUtil;
import util.ValidationUtil;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/** Validated staff self-registration; new identities await administrator approval. */
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserDAO users;

    public UserService() { this(new UserDAO()); }

    public UserService(UserDAO users) {
        this.users = users;
    }

    public void register(String fullName, String username, String email, String phone,
                         char[] password, char[] confirmation) throws RegistrationException {
        try {
            List<String> errors = ValidationUtil.validateRegistration(fullName, username, email,
                    phone, password, confirmation);
            if (!errors.isEmpty()) throw new RegistrationException(String.join("\n", errors));
            if (users.existsByUsername(username.trim())) {
                throw new RegistrationException("That username is already in use.");
            }
            if (users.existsByEmail(email.trim())) {
                throw new RegistrationException("That email address is already in use.");
            }
            String passwordHash = PasswordUtil.hashPassword(password);
            users.createUser(fullName.trim(), username.trim(), email.trim(), phone.trim(),
                    passwordHash, UserRole.RECEPTIONIST, UserStatus.PENDING);
        } catch (SQLException exception) {
            if ("23505".equals(exception.getSQLState())) {
                throw new RegistrationException("That username or email address is already in use.");
            }
            LOGGER.error("Account registration operation failed.", exception);
            throw new RegistrationException("Unable to connect to the hospital database.");
        } finally {
            if (password != null) Arrays.fill(password, '\0');
            if (confirmation != null) Arrays.fill(confirmation, '\0');
        }
    }

    public static class RegistrationException extends Exception {
        public RegistrationException(String message) { super(message); }
    }
}
