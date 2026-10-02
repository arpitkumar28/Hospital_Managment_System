package config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {
    @Test void requiresSecretsAndRedactsThem() {
        assertThrows(IllegalStateException.class, () -> DatabaseConfig.fromEnvironment(Map.of()));
        DatabaseConfig config = DatabaseConfig.fromEnvironment(Map.of(
                "HOSPITAL_DB_USER", "app", "HOSPITAL_DB_PASSWORD", "super-secret"));
        assertFalse(config.toString().contains("super-secret"));
        assertTrue(config.toString().contains("password=<redacted>"));
    }
}
