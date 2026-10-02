package config;

import java.util.Map;

/** Reads PostgreSQL connection settings without exposing credentials in logs. */
public final class DatabaseConfig {
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String sslMode;

    private DatabaseConfig(String host, int port, String database, String username,
                           String password, String sslMode) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.sslMode = sslMode;
    }

    public static DatabaseConfig fromEnvironment() {
        return fromEnvironment(System.getenv());
    }

    static DatabaseConfig fromEnvironment(Map<String, String> values) {
        String host = value(values, "HOSPITAL_DB_HOST", "localhost");
        String database = value(values, "HOSPITAL_DB_NAME", "hospital_management");
        String username = required(values, "HOSPITAL_DB_USER");
        String password = required(values, "HOSPITAL_DB_PASSWORD");
        int port;
        try {
            port = Integer.parseInt(value(values, "HOSPITAL_DB_PORT", "5432"));
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("HOSPITAL_DB_PORT must be a number.");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalStateException("HOSPITAL_DB_PORT must be between 1 and 65535.");
        }
        String sslMode = value(values, "HOSPITAL_DB_SSLMODE", "prefer").toLowerCase(java.util.Locale.ROOT);
        if (!java.util.Set.of("disable", "allow", "prefer", "require", "verify-ca", "verify-full").contains(sslMode)) {
            throw new IllegalStateException("HOSPITAL_DB_SSLMODE is not a supported PostgreSQL SSL mode.");
        }
        return new DatabaseConfig(host, port, database, username, password, sslMode);
    }

    private static String required(Map<String, String> values, String name) {
        String result = values.get(name);
        if (result == null || result.isBlank()) {
            throw new IllegalStateException(name + " environment variable is required.");
        }
        return result;
    }

    private static String value(Map<String, String> values, String name, String fallback) {
        String result = values.get(name);
        return result == null || result.isBlank() ? fallback : result.trim();
    }

    public String host() { return host; }
    public int port() { return port; }
    public String database() { return database; }
    public String username() { return username; }
    public String password() { return password; }
    public String sslMode() { return sslMode; }

    @Override
    public String toString() {
        return "DatabaseConfig[host=" + host + ", port=" + port + ", database=" + database
                + ", username=" + username + ", password=<redacted>, sslMode=" + sslMode + "]";
    }
}
