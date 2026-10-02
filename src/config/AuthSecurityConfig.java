package config;

/** Central account lockout policy. */
public final class AuthSecurityConfig {
    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final int LOCKOUT_MINUTES = 15;

    private AuthSecurityConfig() { }
}
