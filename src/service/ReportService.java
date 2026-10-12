package service;

import dao.ReportDAO;
import model.BillingReport;
import security.AuthorizationService;
import security.SessionManager;

import java.time.LocalDate;

/** Session-authorized financial reporting operations. */
public final class ReportService extends AuthorizedService {
    private final ReportDAO reports;

    public ReportService() {
        this(new ReportDAO());
    }

    public ReportService(ReportDAO reports) {
        this(reports, SessionManager.INSTANCE, new AuthorizationService());
    }

    public ReportService(ReportDAO reports, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.reports = reports;
    }

    public BillingReport getBillingReport(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("Choose a valid inclusive date range.");
        }
        return execute(AuthorizationService.REPORTS, () -> reports.getBillingReport(from, to));
    }
}
