package security;

import model.AuthenticatedUser;
import model.UserRole;

import java.util.EnumSet;
import java.util.Map;

/** Role policy used by application services and the desktop navigation boundary. */
public final class AuthorizationService {
    public static final String DASHBOARD = "Dashboard";
    public static final String PATIENTS = "Patients";
    public static final String DOCTORS = "Doctors";
    public static final String APPOINTMENTS = "Appointments";
    public static final String ADMISSIONS = "Admissions";
    public static final String ROOMS = "Rooms";
    public static final String BEDS = "Beds";
    public static final String BILLING = "Billing";
    public static final String REPORTS = "Reports";
    public static final String USER_MANAGEMENT = "User Management";

    private static final Map<String, EnumSet<UserRole>> MODULE_ROLES = Map.of(
            DASHBOARD, EnumSet.allOf(UserRole.class),
            PATIENTS, EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.DOCTOR),
            DOCTORS, EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST),
            APPOINTMENTS, EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.DOCTOR),
            ADMISSIONS, EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST),
            ROOMS, EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST),
            BEDS, EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST),
            BILLING, EnumSet.of(UserRole.ADMIN, UserRole.ACCOUNTANT),
            REPORTS, EnumSet.of(UserRole.ADMIN, UserRole.ACCOUNTANT),
            USER_MANAGEMENT, EnumSet.of(UserRole.ADMIN));

    public boolean canAccessModule(UserRole role, String module) {
        EnumSet<UserRole> permitted = MODULE_ROLES.get(module);
        return permitted != null && permitted.contains(role);
    }

    public void requireModule(AuthenticatedUser user, String module) {
        if (user == null || !canAccessModule(user.role(), module)) {
            throw new AccessDeniedException("Your account does not have access to this area.");
        }
    }

    public void requireAnyRole(AuthenticatedUser user, UserRole... allowedRoles) {
        if (user == null) {
            throw new AccessDeniedException("Sign-in is required to perform this action.");
        }
        for (UserRole allowedRole : allowedRoles) {
            if (user.role() == allowedRole) return;
        }
        throw new AccessDeniedException("Your account does not have permission to perform this action.");
    }

    public static final class AccessDeniedException extends SecurityException {
        public AccessDeniedException(String message) { super(message); }
    }
}
