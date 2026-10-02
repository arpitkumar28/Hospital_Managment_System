package service;

import dao.AdmissionDAO;
import security.AuthorizationService;
import security.SessionManager;

import java.sql.Date;
import java.util.List;

/** Session-authorized admission and discharge operations. */
public final class AdmissionService extends AuthorizedService {
    private final AdmissionDAO admissions;

    public AdmissionService() { this(new AdmissionDAO()); }
    public AdmissionService(AdmissionDAO admissions) {
        this(admissions, SessionManager.INSTANCE, new AuthorizationService());
    }
    public AdmissionService(AdmissionDAO admissions, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.admissions = admissions;
    }

    public List<Object[]> getPatients() { return execute(AuthorizationService.ADMISSIONS, admissions::getPatients); }
    public List<Object[]> getAvailableBeds() { return execute(AuthorizationService.ADMISSIONS, admissions::getAvailableBeds); }
    public List<Object[]> getAllAdmissions() { return execute(AuthorizationService.ADMISSIONS, admissions::getAllAdmissions); }
    public boolean admitPatient(int patientId, int bedId, Date admissionDate) {
        return execute(AuthorizationService.ADMISSIONS, () -> admissions.admitPatient(patientId, bedId, admissionDate));
    }
    public boolean dischargePatient(int admissionId, Date dischargeDate) {
        return execute(AuthorizationService.ADMISSIONS, () -> admissions.dischargePatient(admissionId, dischargeDate));
    }
}
