package ui;

import dao.DashboardDAO;
import model.AuthenticatedUser;
import model.UserRole;
import security.AuthorizationService;
import security.SessionManager;
import service.AuthService;
import ui.components.AppHeader;
import ui.components.AppSidebar;
import ui.components.StatCard;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;

/** Main administrative shell and database-backed operational overview. */
public class AdminDashboard extends JFrame {
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final AuthenticatedUser currentUser;
    private final AuthorizationService authorization = new AuthorizationService();
    private final AuthService authService = new AuthService();
    private final StatCard patientCard = new StatCard("Total patients", "—", AppTheme.INFO_COLOR);
    private final StatCard doctorCard = new StatCard("Total doctors", "—", AppTheme.SECONDARY_COLOR);
    private final StatCard appointmentCard = new StatCard("Appointments", "—", AppTheme.ACCENT_COLOR);
    private final StatCard pendingCard = new StatCard("Pending appointments", "—", AppTheme.WARNING_COLOR);

    /** @deprecated Start the dashboard only after authenticating a staff user. */
    @Deprecated
    public AdminDashboard() {
        this(SessionManager.INSTANCE.getCurrentUser().orElseThrow(
                () -> new IllegalStateException("Sign-in is required.")));
    }

    public AdminDashboard(AuthenticatedUser currentUser) {
        super("Hospital Management System");
        this.currentUser = java.util.Objects.requireNonNull(currentUser);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1240, 780);
        setLocationRelativeTo(null);
        createUI();
        refreshStatistics();
        setVisible(true);
    }

    private void createUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND_COLOR);

        JPanel body = new JPanel(new BorderLayout());
        body.add(createSidebar(), BorderLayout.WEST);
        body.add(createDashboardContent(), BorderLayout.CENTER);

        root.add(new AppHeader("Hospital Management System", currentUser.fullName(), currentUser.role().name()), BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    private AppSidebar createSidebar() {
        AppSidebar sidebar = new AppSidebar(this::navigate, this::logout);
        sidebar.addNavigationItem("Dashboard");
        addIfAuthorized(sidebar, "Patients");
        addIfAuthorized(sidebar, "Doctors");
        addIfAuthorized(sidebar, "Appointments");
        sidebar.setSelected("Dashboard");
        return sidebar;
    }

    private void addIfAuthorized(AppSidebar sidebar, String module) {
        if (authorization.canAccessModule(currentUser.role(), module)) sidebar.addNavigationItem(module);
    }

    private JPanel createDashboardContent() {
        JPanel content = new JPanel(new BorderLayout(0, AppTheme.SPACE_LG));
        content.setBackground(AppTheme.BACKGROUND_COLOR);
        content.setBorder(AppTheme.pagePadding());

        JPanel intro = new JPanel(new BorderLayout());
        intro.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Hospital overview and operational summary");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new javax.swing.BoxLayout(text, javax.swing.BoxLayout.Y_AXIS));
        text.add(title);
        text.add(javax.swing.Box.createVerticalStrut(AppTheme.SPACE_XS));
        text.add(subtitle);
        intro.add(text, BorderLayout.WEST);

        JPanel cards = new JPanel(new GridLayout(2, 2, AppTheme.SPACE_LG, AppTheme.SPACE_LG));
        cards.setOpaque(false);
        cards.add(patientCard);
        cards.add(doctorCard);
        cards.add(appointmentCard);
        cards.add(pendingCard);

        content.add(intro, BorderLayout.NORTH);
        content.add(cards, BorderLayout.CENTER);
        return content;
    }

    private void navigate(String page) {
        if (!authorization.canAccessModule(currentUser.role(), page)) {
            showFriendlyError("Your account does not have access to this area.");
            return;
        }
        switch (page) {
            case "Dashboard" -> refreshStatistics();
            case "Patients" -> openModule("Patient Management", PatientPanel::new);
            case "Doctors" -> openModule("Doctor Management", DoctorPanel::new);
            case "Appointments" -> openModule("Appointment Management", AppointmentPanel::new);
            default -> { }
        }
    }

    private void refreshStatistics() {
        if (currentUser.role() != UserRole.ADMIN) {
            patientCard.setValue("—");
            doctorCard.setValue("—");
            appointmentCard.setValue("—");
            pendingCard.setValue("—");
            return;
        }
        try {
            patientCard.setValue(String.valueOf(dashboardDAO.getTotalPatients()));
            doctorCard.setValue(String.valueOf(dashboardDAO.getTotalDoctors()));
            appointmentCard.setValue(String.valueOf(dashboardDAO.getTotalAppointments()));
            pendingCard.setValue(String.valueOf(dashboardDAO.getPendingAppointments()));
        } catch (RuntimeException exception) {
            showFriendlyError("Unable to load dashboard information. Verify the PostgreSQL connection settings.");
        }
    }

    private void openModule(String title, Runnable opener) {
        try {
            opener.run();
        } catch (RuntimeException exception) {
            showFriendlyError("Unable to open " + title + ".");
        }
    }

    private void showFriendlyError(String message) {
        JOptionPane.showMessageDialog(this, message, "Hospital Management System", JOptionPane.ERROR_MESSAGE);
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?",
                "Sign out", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            SwingUtilities.invokeLater(LoginFrame::new);
        }
    }

    public static void main(String[] args) {
        AppTheme.install();
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
