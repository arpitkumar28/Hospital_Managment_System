package dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AdmissionDAOTest {
    private final AdmissionDAO admissions = new AdmissionDAO();

    @Test
    void admitPatientRejectsNullAdmissionDateBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class,
                () -> admissions.admitPatient(1, 1, null));
    }

    @Test
    void dischargePatientRejectsNullDischargeDateBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class,
                () -> admissions.dischargePatient(1, null));
    }
}
