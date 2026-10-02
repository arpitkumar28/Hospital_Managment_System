package security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test void hashesAndVerifiesWithoutStoringPlaintext() {
        char[] password = "HealthyPass9!".toCharArray();
        String hash = PasswordUtil.hashPassword(password);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"));
        assertNotEquals(new String(password), hash);
        assertTrue(PasswordUtil.verifyPassword(password, hash));
        assertFalse(PasswordUtil.verifyPassword("wrong".toCharArray(), hash));
    }
}
