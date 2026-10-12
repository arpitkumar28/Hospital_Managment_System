package ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoginFrameTest {
    @Test void patientLoginUsesPatientDescriptionOutsideStaffRoleList() {
        assertEquals("Patient access to your healthcare workspace.",
                LoginFrame.descriptionForRole("PATIENT"));
    }

    @Test void staffLoginDescriptionsRemainMappedToTheirRoles() {
        assertEquals("Administrator access", LoginFrame.descriptionForRole("ADMIN"));
        assertEquals("Front desk and patient operations", LoginFrame.descriptionForRole("RECEPTIONIST"));
        assertEquals("Clinical and appointment workspace", LoginFrame.descriptionForRole("DOCTOR"));
        assertEquals("Billing and financial workspace", LoginFrame.descriptionForRole("ACCOUNTANT"));
    }

    @Test void unknownLoginTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> LoginFrame.descriptionForRole("UNKNOWN"));
    }
}
