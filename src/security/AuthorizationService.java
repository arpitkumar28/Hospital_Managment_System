package security;

import model.AuthenticatedUser;
import model.UserRole;

import java.util.EnumSet;
import java.util.Map;

/** Role policy used by application services and the desktop navigation boundary. */
public final class AuthorizationService {
    private static final Map<String, EnumSet<UserRole>> MODULE_ROLES = Map.of(
            "Dashboard", EnumSet.allOf(UserRole.class),
            "Patients", EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.DOCTOR),
            "Doctors", EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST),
            "Appointments", EnumSet.of(UserRole.ADMIN, UserRole.RECEPTIONIST, UserRole.DOCTOR),
            "Billing", EnumSet.of(UserRole.ADMIN, UserRole.ACCOUNTANT),
            "Reports", EnumSet.of(UserRole.ADMIN, UserRole.ACCOUNTANT));

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
