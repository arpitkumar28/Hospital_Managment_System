package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {
    @Test void validatesRegistrationAndPasswordRequirements() {
        assertTrue(ValidationUtil.validateRegistration("A Person", "person_01", "person@example.org",
                "+1 212 555 0100", "StrongPass9!".toCharArray(), "StrongPass9!".toCharArray()).isEmpty());
        assertFalse(ValidationUtil.isStrongPassword("weakpass".toCharArray()));
        assertFalse(ValidationUtil.isValidLogin(" ", "x".toCharArray()));
    }
}
