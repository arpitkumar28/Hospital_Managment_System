package model;

import java.time.Instant;

/** Session-safe user identity. Deliberately excludes password and password hash. */
public record AuthenticatedUser(
        long userId,
        String username,
        String fullName,
        UserRole role,
        Instant loginTime) {
}
