package util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?(?:\\.[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?)+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._-]{3,80}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9][0-9 .()\\-]{5,38}$");

    private ValidationUtil() { }

    public static List<String> validateRegistration(String fullName, String username, String email,
                                                    String phone, char[] password, char[] confirmation) {
        List<String> errors = new ArrayList<>();
        if (fullName == null || fullName.isBlank() || fullName.trim().length() > 150) {
            errors.add("Enter your full name (up to 150 characters).");
        }
        if (username == null || !USERNAME.matcher(username.trim()).matches()) {
            errors.add("Username must be 3–80 characters using letters, numbers, dots, underscores or hyphens.");
        }
        if (email == null || email.length() > 254 || !EMAIL.matcher(email.trim()).matches()) {
            errors.add("Enter a valid email address.");
        }
        if (phone == null || !PHONE.matcher(phone.trim()).matches()) {
            errors.add("Enter a valid phone number.");
        }
        if (!isStrongPassword(password)) {
            errors.add("Password must be at least 8 characters and include uppercase, lowercase, a number and a special character.");
        }
        if (password == null || confirmation == null || !java.util.Arrays.equals(password, confirmation)) {
            errors.add("Passwords do not match.");
        }
        return List.copyOf(errors);
    }

    public static boolean isValidLogin(String usernameOrEmail, char[] password) {
        return usernameOrEmail != null && !usernameOrEmail.trim().isEmpty()
                && password != null && password.length > 0;
    }

    public static boolean isStrongPassword(char[] password) {
        if (password == null || password.length < 8) return false;
        boolean upper = false, lower = false, number = false, special = false;
        for (char character : password) {
            upper |= Character.isUpperCase(character);
            lower |= Character.isLowerCase(character);
            number |= Character.isDigit(character);
            special |= !Character.isLetterOrDigit(character);
        }
        return upper && lower && number && special;
    }
}
