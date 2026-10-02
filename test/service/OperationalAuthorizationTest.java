package service;

import dao.*;
import model.User;
import model.UserRole;
import model.UserStatus;
import org.junit.jupiter.api.Test;
import security.AuthorizationService;
import security.SessionManager;

import java.sql.Date;
import java.time.Clock;
import java.util.List;
import java.util.function.IntSupplier;

import static org.junit.jupiter.api.Assertions.*;

/** Verifies module policy at the service boundary and that denial precedes DAO calls. */
class OperationalAuthorizationTest {
    private static final AuthorizationService POLICY = new AuthorizationService();

    @Test void patientOperations() {
        PatientFake d = new PatientFake(); SessionManager s = session(); PatientService x = new PatientService(d, s, POLICY);
        verify(s, "Patients", new IntSupplier[]{() -> d.calls},
                () -> x.addPatient("a","b","","","","","",""), x::getAllPatients,
                () -> x.updatePatient(1,"a","b","","","","","",""), () -> x.deletePatient(1));
    }
    @Test void doctorOperations() {
        DoctorFake d = new DoctorFake(); SessionManager s = session(); DoctorService x = new DoctorService(d,s,POLICY);
        verify(s,"Doctors",new IntSupplier[]{() -> d.calls},
                () -> x.addDoctor("a","b","","","1","2",""),x::getAllDoctors,
                () -> x.updateDoctor(1,"a","b","","","1","2",""),() -> x.deleteDoctor(1));
    }
    @Test void appointmentAndLookupOperations() {
        AppointmentFake a = new AppointmentFake(); PatientFake p = new PatientFake(); DoctorFake d = new DoctorFake();
        SessionManager s = session(); AppointmentService x = new AppointmentService(a,p,d,s,POLICY);
        verify(s,"Appointments",new IntSupplier[]{() -> a.calls,() -> p.calls,() -> d.calls},
                x::getPatients,x::getDoctors,x::getAllAppointments,() -> x.addAppointment(1,2,"","","",""),
                () -> x.updateAppointment(1,1,2,"","","",""),() -> x.deleteAppointment(1));
    }
    @Test void admissionOperations() {
        AdmissionFake d = new AdmissionFake(); SessionManager s = session(); AdmissionService x = new AdmissionService(d,s,POLICY);
        verify(s,"Admissions",new IntSupplier[]{() -> d.calls},x::getPatients,x::getAvailableBeds,x::getAllAdmissions,
                () -> x.admitPatient(1,2,Date.valueOf("2026-01-01")),() -> x.dischargePatient(1,Date.valueOf("2026-01-02")));
    }
    @Test void roomOperations() {
        RoomFake d = new RoomFake(); SessionManager s = session(); RoomService x = new RoomService(d,s,POLICY);
        verify(s,"Rooms",new IntSupplier[]{() -> d.calls},x::getAllRooms,() -> x.addRoom("1","General",10),
                () -> x.updateRoom(1,"1","General",10),() -> x.deleteRoom(1),() -> x.roomExists("1"),x::getRoomCount);
    }
    @Test void bedOperations() {
        BedFake b = new BedFake(); RoomFake r = new RoomFake(); SessionManager s = session(); BedService x = new BedService(b,r,s,POLICY);
        verify(s,"Beds",new IntSupplier[]{() -> b.calls,() -> r.calls},x::getAllRooms,x::getAllBeds,x::getAvailableBeds,
                () -> x.addBed(1,"1"),() -> x.updateBed(1,1,"1","AVAILABLE"),() -> x.updateBedStatus(1,"AVAILABLE"),
                () -> x.deleteBed(1),x::getBedCount,x::getAvailableBedCount,x::getOccupiedBedCount);
    }
    @Test void billingAndPaymentOperations() {
        BillFake d = new BillFake(); SessionManager s = session(); BillingService x = new BillingService(d,s,POLICY);
        verify(s,"Billing",new IntSupplier[]{() -> d.calls},() -> x.addBill(1,2,1,1,1,1,1),
                () -> x.updateBill(1,1,2,1,1,1,1,1),() -> x.deleteBill(1),x::getAllBills,() -> x.getBillsByPatient(1),
                () -> x.getBillById(1),() -> x.addPayment(1,1),() -> x.getRemainingAmount(1),() -> x.calculateTotal(1,1,1,1));
    }
    @Test void dashboardCountsAreAdministratorOnly() {
        DashboardFake d = new DashboardFake(); SessionManager s = session(); DashboardService x = new DashboardService(d,s,POLICY);
        verify(s,"Dashboard",new IntSupplier[]{() -> d.calls},x::getTotalPatients,x::getTotalDoctors,x::getTotalAppointments,x::getPendingAppointments);
    }

    private static void verify(SessionManager sessions, String module, IntSupplier[] counts, Runnable... operations) {
        for (UserRole role : UserRole.values()) {
            sessions.logout(); sessions.createSession(user(role));
            for (int i=0; i<operations.length; i++) {
                final int operationIndex = i;
                int[] before = snapshot(counts);
                boolean allowed = POLICY.canAccessModule(role,module) &&
                        (!module.equals("Dashboard") || role == UserRole.ADMIN);
                if (allowed) {
                    assertDoesNotThrow(() -> operations[operationIndex].run(),module+" op "+i+" for "+role);
                    int[] expected=before.clone(); expected[target(module,i)]++;
                    assertArrayEquals(expected,snapshot(counts),module+" op "+i+" DAO call");
                } else {
                    assertThrows(AuthorizationService.AccessDeniedException.class,operations[i]::run,module+" op "+i+" for "+role);
                    assertArrayEquals(before,snapshot(counts),module+" denied before DAO");
                }
            }
        }
        sessions.logout();
        for (Runnable operation:operations) {
            int[] before=snapshot(counts);
            assertThrows(AuthorizationService.AccessDeniedException.class,operation::run,module+" unauthenticated");
            assertArrayEquals(before,snapshot(counts),module+" unauthenticated before DAO");
        }
    }
    private static int target(String module,int operation) {
        if (module.equals("Appointments") && operation==0) return 1;
        if (module.equals("Appointments") && operation==1) return 2;
        if (module.equals("Beds") && operation==0) return 1;
        return 0;
    }
    private static int[] snapshot(IntSupplier[] counts) { int[] x=new int[counts.length]; for(int i=0;i<x.length;i++)x[i]=counts[i].getAsInt(); return x; }
    private static SessionManager session(){return new SessionManager(Clock.systemUTC());}
    private static User user(UserRole role){return new User(1,"staff","staff@example.org","hash","Staff","",role,UserStatus.ACTIVE,0,null);}

    private static class PatientFake extends PatientDAO { int calls;
        @Override public boolean addPatient(String a,String b,String c,String d,String e,String f,String g,String h){calls++;return true;}
        @Override public List<Object[]> getAllPatients(){calls++;return java.util.Collections.singletonList(new Object[]{1,"Patient"});}
        @Override public boolean updatePatient(int i,String a,String b,String c,String d,String e,String f,String g,String h){calls++;return true;}
        @Override public boolean deletePatient(int i){calls++;return true;}}
    private static class DoctorFake extends DoctorDAO { int calls;
        @Override public boolean addDoctor(String a,String b,String c,String d,String e,String f,String g){calls++;return true;}
        @Override public List<Object[]> getAllDoctors(){calls++;return java.util.Collections.singletonList(new Object[]{1,"Doctor","Specialty"});}
        @Override public boolean updateDoctor(int i,String a,String b,String c,String d,String e,String f,String g){calls++;return true;}
        @Override public boolean deleteDoctor(int i){calls++;return true;}}
    private static class AppointmentFake extends AppointmentDAO { int calls;
        @Override public List<Object[]> getAllAppointments(){calls++;return List.of();}
        @Override public boolean addAppointment(int a,int b,String c,String d,String e,String f){calls++;return true;}
        @Override public boolean updateAppointment(int i,int a,int b,String c,String d,String e,String f){calls++;return true;}
        @Override public boolean deleteAppointment(int i){calls++;return true;}}
    private static class AdmissionFake extends AdmissionDAO { int calls;
        @Override public List<Object[]> getPatients(){calls++;return List.of();}
        @Override public List<Object[]> getAvailableBeds(){calls++;return List.of();}
        @Override public List<Object[]> getAllAdmissions(){calls++;return List.of();}
        @Override public boolean admitPatient(int a,int b,Date c){calls++;return true;}
        @Override public boolean dischargePatient(int a,Date b){calls++;return true;}}
    private static class RoomFake extends RoomDAO { int calls;
        @Override public List<Object[]> getAllRooms(){calls++;return List.of();}
        @Override public boolean addRoom(String a,String b,double c){calls++;return true;}
        @Override public boolean updateRoom(int i,String a,String b,double c){calls++;return true;}
        @Override public boolean deleteRoom(int i){calls++;return true;}
        @Override public boolean roomExists(String a){calls++;return true;}
        @Override public int getRoomCount(){calls++;return 1;}}
    private static class BedFake extends BedDAO { int calls;
        @Override public List<Object[]> getAllBeds(){calls++;return List.of();}
        @Override public List<Object[]> getAvailableBeds(){calls++;return List.of();}
        @Override public boolean addBed(int a,String b){calls++;return true;}
        @Override public boolean updateBed(int a,int b,String c,String d){calls++;return true;}
        @Override public boolean updateBedStatus(int a,String b){calls++;return true;}
        @Override public boolean deleteBed(int a){calls++;return true;}
        @Override public int getBedCount(){calls++;return 1;}
        @Override public int getAvailableBedCount(){calls++;return 1;}
        @Override public int getOccupiedBedCount(){calls++;return 1;}}
    private static class BillFake extends BillDAO { int calls;
        @Override public boolean addBill(int a,int b,double c,double d,double e,double f,double g){calls++;return true;}
        @Override public boolean updateBill(int i,int a,int b,double c,double d,double e,double f,double g){calls++;return true;}
        @Override public boolean deleteBill(int i){calls++;return true;}
        @Override public List<Object[]> getAllBills(){calls++;return List.of();}
        @Override public List<Object[]> getBillsByPatient(int i){calls++;return List.of();}
        @Override public Object[] getBillById(int i){calls++;return new Object[]{i};}
        @Override public boolean addPayment(int i,double a){calls++;return true;}
        @Override public double getRemainingAmount(int i){calls++;return 1;}
        @Override public double calculateTotal(double a,double b,double c,double d){calls++;return a+b+c+d;}}
    private static class DashboardFake extends DashboardDAO { int calls;
        @Override public int getTotalPatients(){calls++;return 1;}
        @Override public int getTotalDoctors(){calls++;return 1;}
        @Override public int getTotalAppointments(){calls++;return 1;}
        @Override public int getPendingAppointments(){calls++;return 1;}}
}
