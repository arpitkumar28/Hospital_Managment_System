package service;

import dao.DashboardDAO;
import model.AuthenticatedUser;
import model.UserRole;
import security.AuthorizationService;
import security.SessionManager;

/** Session-authorized dashboard summaries. Operational counts remain administrator-only. */
public final class DashboardService extends AuthorizedService {
    private final DashboardDAO dashboard;

    public DashboardService() { this(new DashboardDAO()); }
    public DashboardService(DashboardDAO dashboard) {
        this(dashboard, SessionManager.INSTANCE, new AuthorizationService());
    }
    public DashboardService(DashboardDAO dashboard, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.dashboard = dashboard;
    }

    private void requireAdministrator() {
        AuthenticatedUser actor = requireModule(AuthorizationService.DASHBOARD);
        authorization.requireAnyRole(actor, UserRole.ADMIN);
    }

    public int getTotalPatients() { requireAdministrator(); return dashboard.getTotalPatients(); }
    public int getTotalDoctors() { requireAdministrator(); return dashboard.getTotalDoctors(); }
    public int getTotalAppointments() { requireAdministrator(); return dashboard.getTotalAppointments(); }
    public int getPendingAppointments() { requireAdministrator(); return dashboard.getPendingAppointments(); }
}
