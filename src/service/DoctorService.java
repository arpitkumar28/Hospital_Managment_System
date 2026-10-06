package service;

import dao.DoctorDAO;
import security.AuthorizationService;
import security.SessionManager;

import java.util.List;

/** Session-authorized doctor directory operations. */
public final class DoctorService extends AuthorizedService {
    private final DoctorDAO doctors;

    public DoctorService() { this(new DoctorDAO()); }
    public DoctorService(DoctorDAO doctors) { this(doctors, SessionManager.INSTANCE, new AuthorizationService()); }
    public DoctorService(DoctorDAO doctors, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.doctors = doctors;
    }

    public boolean addDoctor(String name, String specialization, String phone, String email,
                             String experience, String consultationFee, String availability) {
        return execute(AuthorizationService.DOCTORS, () -> doctors.addDoctor(name, specialization, phone,
                email, experience, consultationFee, availability));
    }
    public List<Object[]> getAllDoctors() {
        return execute(AuthorizationService.DOCTORS, doctors::getAllDoctors);
    }
    public boolean updateDoctor(int doctorId, String name, String specialization, String phone, String email,
                                String experience, String consultationFee, String availability) {
        return execute(AuthorizationService.DOCTORS, () -> doctors.updateDoctor(doctorId, name, specialization,
                phone, email, experience, consultationFee, availability));
    }
    public boolean deleteDoctor(int doctorId) {
        return execute(AuthorizationService.DOCTORS, () -> doctors.deleteDoctor(doctorId));
    }
}
