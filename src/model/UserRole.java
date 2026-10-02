package model;

public enum UserRole {
    ADMIN,
    RECEPTIONIST,
    DOCTOR,
    ACCOUNTANT;

    public static UserRole fromDatabase(String value) {
        return UserRole.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
    }
}
