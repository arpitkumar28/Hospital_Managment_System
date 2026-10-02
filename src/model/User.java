package model;

import java.time.Instant;

/** Persisted staff account; never contains a raw password. */
public record User(
        long userId,
        String username,
        String email,
        String passwordHash,
        String fullName,
        String phone,
        UserRole role,
        UserStatus status,
        int failedLoginAttempts,
        Instant lockedUntil) {

    public User withStatus(UserStatus newStatus, int attempts, Instant newLockedUntil) {
        return new User(userId, username, email, passwordHash, fullName, phone,
                role, newStatus, attempts, newLockedUntil);
    }
}
