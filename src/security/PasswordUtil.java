package security;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;

/** BCrypt helpers. Callers remain responsible for clearing password arrays. */
public final class PasswordUtil {
    private static final int WORK_FACTOR = 12;

    private PasswordUtil() { }

    public static String hashPassword(char[] password) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password is required.");
        }
        char[] copy = Arrays.copyOf(password, password.length);
        try {
            return BCrypt.hashpw(new String(copy), BCrypt.gensalt(WORK_FACTOR));
        } finally {
            Arrays.fill(copy, '\0');
        }
    }

    public static boolean verifyPassword(char[] candidate, String hash) {
        if (candidate == null || candidate.length == 0 || hash == null || hash.isBlank()) {
            return false;
        }
        char[] copy = Arrays.copyOf(candidate, candidate.length);
        try {
            return BCrypt.checkpw(new String(copy), hash);
        } catch (IllegalArgumentException malformedHash) {
            return false;
        } finally {
            Arrays.fill(copy, '\0');
        }
    }
}
