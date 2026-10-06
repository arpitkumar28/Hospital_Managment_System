package service;

import dao.PatientDAO;
import security.AuthorizationService;
import security.SessionManager;

import java.util.List;

/** Session-authorized patient operations. */
public final class PatientService extends AuthorizedService {
    private final PatientDAO patients;

    public PatientService() { this(new PatientDAO()); }
    public PatientService(PatientDAO patients) {
        this(patients, SessionManager.INSTANCE, new AuthorizationService());
    }
    public PatientService(PatientDAO patients, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.patients = patients;
    }

    public boolean addPatient(String name, String gender, String dob, String phone, String email,
                              String address, String bloodGroup, String emergencyContact) {
        return execute(AuthorizationService.PATIENTS, () -> patients.addPatient(name, gender, dob, phone,
                email, address, bloodGroup, emergencyContact));
    }
    public List<Object[]> getAllPatients() {
        return execute(AuthorizationService.PATIENTS, patients::getAllPatients);
    }
    public boolean updatePatient(int patientId, String name, String gender, String dob, String phone,
                                String email, String address, String bloodGroup, String emergencyContact) {
        return execute(AuthorizationService.PATIENTS, () -> patients.updatePatient(patientId, name, gender,
                dob, phone, email, address, bloodGroup, emergencyContact));
    }
    public boolean deletePatient(int patientId) {
        return execute(AuthorizationService.PATIENTS, () -> patients.deletePatient(patientId));
    }
}
