package config;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {
    @Test void requiresSecretsAndRedactsThem() {
        IllegalStateException missing = assertThrows(IllegalStateException.class,
                () -> DatabaseConfig.fromEnvironment(Map.of()));
        assertTrue(missing.getMessage().contains("HOSPITAL_DB_USER"));
        assertTrue(missing.getMessage().contains(".env.local"));
        DatabaseConfig config = DatabaseConfig.fromEnvironment(Map.of(
                "HOSPITAL_DB_USER", "app", "HOSPITAL_DB_PASSWORD", "super-secret"));
        assertFalse(config.toString().contains("super-secret"));
        assertTrue(config.toString().contains("password=<redacted>"));
    }

    @Test void loadsDatabaseSettingsFromLocalEnvFile(@org.junit.jupiter.api.io.TempDir Path directory) throws Exception {
        Path envFile = directory.resolve(".env.local");
        Files.writeString(envFile, "# local test settings\n"
                + "HOSPITAL_DB_HOST=127.0.0.1\n"
                + "HOSPITAL_DB_PORT=5544\n"
                + "HOSPITAL_DB_NAME=hospital_management_test\n"
                + "HOSPITAL_DB_USER=test_user\n"
                + "HOSPITAL_DB_PASSWORD='test-secret-value'\n"
                + "HOSPITAL_DB_SSLMODE=disable\n");

        DatabaseConfig config = DatabaseConfig.fromEnvironment(Map.of(), envFile);

        assertEquals("127.0.0.1", config.host());
        assertEquals(5544, config.port());
        assertEquals("hospital_management_test", config.database());
        assertEquals("test_user", config.username());
        assertEquals("test-secret-value", config.password());
        assertEquals("disable", config.sslMode());
        assertFalse(config.toString().contains("test-secret-value"));
    }

    @Test void processEnvironmentOverridesLocalEnvFile(@org.junit.jupiter.api.io.TempDir Path directory)
            throws Exception {
        Path envFile = directory.resolve(".env.local");
        Files.writeString(envFile, "HOSPITAL_DB_USER=file_user\nHOSPITAL_DB_PASSWORD=file_secret\n"
                + "HOSPITAL_DB_NAME=file_database\n");

        DatabaseConfig config = DatabaseConfig.fromEnvironment(Map.of(
                "HOSPITAL_DB_USER", "environment_user",
                "HOSPITAL_DB_PASSWORD", "environment_secret"), envFile);

        assertEquals("environment_user", config.username());
        assertEquals("environment_secret", config.password());
        assertEquals("file_database", config.database());
    }

    @Test void reportsMalformedEnvFileWithoutEchoingItsContents(@org.junit.jupiter.api.io.TempDir Path directory)
            throws Exception {
        Path envFile = directory.resolve(".env.local");
        Files.writeString(envFile, "not-a-key-value-line\npassword=do-not-echo-this\n");

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> DatabaseConfig.fromEnvironment(Map.of(), envFile));

        assertTrue(error.getMessage().contains("line 1"));
        assertFalse(error.getMessage().contains("do-not-echo-this"));
    }
}
