package config;

import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.HashMap;

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
        String configuredPath = System.getProperty("hospital.env.file", ".env.local");
        Path envFile = Path.of(configuredPath);
        return fromEnvironment(System.getenv(), envFile);
    }

    static DatabaseConfig fromEnvironment(Map<String, String> values) {
        return fromEnvironment(values, null);
    }

    static DatabaseConfig fromEnvironment(Map<String, String> environment, Path envFile) {
        Map<String, String> values = new HashMap<>();
        if (envFile != null) {
            values.putAll(readEnvFile(envFile));
        }
        // The process environment deliberately takes precedence over local file settings.
        values.putAll(environment);

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
            throw new IllegalStateException(name + " is required. Set it in the process environment or "
                    + "in .env.local. Copy .env.example to .env.local and fill in the local database credentials.");
        }
        return result;
    }

    private static Map<String, String> readEnvFile(Path envFile) {
        if (!Files.exists(envFile)) return Map.of();
        if (!Files.isRegularFile(envFile)) {
            throw new IllegalStateException("Database configuration path is not a regular file: " + envFile);
        }

        Map<String, String> result = new HashMap<>();
        try {
            int lineNumber = 0;
            for (String line : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
                lineNumber++;
                String entry = line.trim();
                if (entry.isEmpty() || entry.startsWith("#")) continue;
                int separator = entry.indexOf('=');
                if (separator <= 0) {
                    throw malformedEnvFile(envFile, lineNumber);
                }
                String key = entry.substring(0, separator).trim();
                if (!key.matches("[A-Za-z_][A-Za-z0-9_]*")) {
                    throw malformedEnvFile(envFile, lineNumber);
                }
                String value = entry.substring(separator + 1).trim();
                if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\""))
                        || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                } else if (value.startsWith("\"") || value.startsWith("'")
                        || value.endsWith("\"") || value.endsWith("'")) {
                    throw malformedEnvFile(envFile, lineNumber);
                }
                result.put(key, value);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read database configuration file " + envFile
                    + ". Check that the file exists and is readable.", exception);
        }
        return result;
    }

    private static IllegalStateException malformedEnvFile(Path envFile, int lineNumber) {
        return new IllegalStateException("Invalid KEY=value entry in " + envFile + " at line " + lineNumber + ".");
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
