package model;

import java.time.Instant;

/** Non-secret staff identity projection for account administration screens. */
public record StaffAccountSummary(long userId, String username, String email, String fullName,
                                  String phone, UserRole role, UserStatus status,
                                  Instant createdAt, Instant lastLogin) { }
