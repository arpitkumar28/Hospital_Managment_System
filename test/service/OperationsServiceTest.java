package service;

import dao.AdmissionDAO;
import dao.AppointmentDAO;
import dao.BedDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import dao.RoomDAO;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.Test;
import security.AuthorizationService;
import security.SessionManager;

import java.sql.Date;
import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperationsServiceTest {
    private static final AuthorizationService POLICY = new AuthorizationService();

    @Test void appointmentCrudAndLookupsAreDelegatedForAuthorizedRole() {
        SessionManager sessions = session(UserRole.RECEPTIONIST);
        AppointmentFake dao = new AppointmentFake();
        AppointmentService service = new AppointmentService(dao, new PatientDAO(), new DoctorDAO(), sessions, POLICY);

        assertEquals(List.of(), service.getAllAppointments());
        assertTrue(service.addAppointment(1, 2, "2026-10-12", "09:00:00", "checkup", "PENDING"));
        assertTrue(service.updateAppointment(3, 1, 2, "2026-10-13", "10:00:00", "review", "CONFIRMED"));
        assertTrue(service.deleteAppointment(3));
        assertEquals(4, dao.calls);
    }

    @Test void unauthorizedAppointmentRequestDoesNotReachDao() {
        AppointmentFake dao = new AppointmentFake();
        AppointmentService service = new AppointmentService(dao, new PatientDAO(), new DoctorDAO(),
                session(UserRole.ACCOUNTANT), POLICY);
        assertThrows(AuthorizationService.AccessDeniedException.class, service::getAllAppointments);
        assertEquals(0, dao.calls);
    }

    @Test void admissionAndDischargeAreDelegated() {
        AdmissionFake dao = new AdmissionFake();
        AdmissionService service = new AdmissionService(dao, session(UserRole.ADMIN), POLICY);
        assertTrue(service.admitPatient(1, 2, Date.valueOf("2026-10-12")));
        assertTrue(service.dischargePatient(4, Date.valueOf("2026-10-14")));
        assertEquals(2, dao.calls);
    }

    @Test void roomAndBedMutationsAreDelegated() {
        SessionManager sessions = session(UserRole.RECEPTIONIST);
        RoomFake roomDao = new RoomFake();
        RoomService rooms = new RoomService(roomDao, sessions, POLICY);
        assertTrue(rooms.addRoom("101", "General", 50));
        assertTrue(rooms.updateRoom(1, "101", "General", 55));
        assertTrue(rooms.deleteRoom(1));
        assertEquals(3, roomDao.calls);

        BedFake bedDao = new BedFake();
        BedService beds = new BedService(bedDao, roomDao, sessions, POLICY);
        assertEquals(List.of(), beds.getAvailableBeds());
        assertTrue(beds.addBed(1, "A"));
        assertTrue(beds.updateBed(2, 1, "A", "OCCUPIED"));
        assertTrue(beds.deleteBed(2));
        assertEquals(4, bedDao.calls);
    }

    private static SessionManager session(UserRole role) {
        SessionManager sessions = new SessionManager(Clock.systemUTC());
        sessions.createSession(new User(1, "staff", "staff@example.org", "hash", "Staff", "", role,
                UserStatus.ACTIVE, 0, null));
        return sessions;
    }

    private static final class AppointmentFake extends AppointmentDAO {
        int calls;
        @Override public List<Object[]> getAllAppointments() { calls++; return List.of(); }
        @Override public boolean addAppointment(int p,int d,String date,String time,String reason,String status) { calls++; return true; }
        @Override public boolean updateAppointment(int id,int p,int d,String date,String time,String reason,String status) { calls++; return true; }
        @Override public boolean deleteAppointment(int id) { calls++; return true; }
    }
    private static final class AdmissionFake extends AdmissionDAO {
        int calls;
        @Override public boolean admitPatient(int p,int b,Date date) { calls++; return true; }
        @Override public boolean dischargePatient(int a,Date date) { calls++; return true; }
    }
    private static final class RoomFake extends RoomDAO {
        int calls;
        @Override public boolean addRoom(String n,String t,double p) { calls++; return true; }
        @Override public boolean updateRoom(int id,String n,String t,double p) { calls++; return true; }
        @Override public boolean deleteRoom(int id) { calls++; return true; }
    }
    private static final class BedFake extends BedDAO {
        int calls;
        @Override public List<Object[]> getAvailableBeds() { calls++; return List.of(); }
        @Override public boolean addBed(int room,String number) { calls++; return true; }
        @Override public boolean updateBed(int id,int room,String number,String status) { calls++; return true; }
        @Override public boolean deleteBed(int id) { calls++; return true; }
    }
}
