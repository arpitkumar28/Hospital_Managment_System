package service;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import security.AuthorizationService;
import security.SessionManager;

import java.util.List;

/** Session-authorized appointment operations and selection data. */
public final class AppointmentService extends AuthorizedService {
    private final AppointmentDAO appointments;
    private final PatientDAO patients;
    private final DoctorDAO doctors;

    public AppointmentService() { this(new AppointmentDAO(), new PatientDAO(), new DoctorDAO()); }
    public AppointmentService(AppointmentDAO appointments, PatientDAO patients, DoctorDAO doctors) {
        this(appointments, patients, doctors, SessionManager.INSTANCE, new AuthorizationService());
    }
    public AppointmentService(AppointmentDAO appointments, PatientDAO patients, DoctorDAO doctors,
                              SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.appointments = appointments;
        this.patients = patients;
        this.doctors = doctors;
    }

    public List<Object[]> getPatients() {
        return execute(AuthorizationService.APPOINTMENTS, patients::getAllPatients);
    }
    public List<Object[]> getDoctors() {
        return execute(AuthorizationService.APPOINTMENTS, doctors::getAllDoctors);
    }
    public List<Object[]> getAllAppointments() {
        return execute(AuthorizationService.APPOINTMENTS, appointments::getAllAppointments);
    }
    public boolean addAppointment(int patientId, int doctorId, String date, String time, String reason, String status) {
        return execute(AuthorizationService.APPOINTMENTS,
                () -> appointments.addAppointment(patientId, doctorId, date, time, reason, status));
    }
    public boolean updateAppointment(int appointmentId, int patientId, int doctorId, String date, String time,
                                    String reason, String status) {
        return execute(AuthorizationService.APPOINTMENTS,
                () -> appointments.updateAppointment(appointmentId, patientId, doctorId, date, time, reason, status));
    }
    public boolean deleteAppointment(int appointmentId) {
        return execute(AuthorizationService.APPOINTMENTS, () -> appointments.deleteAppointment(appointmentId));
    }
}
