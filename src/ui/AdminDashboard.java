package ui;

import dao.DashboardDAO;
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
    private final StatCard patientCard = new StatCard("Total patients", "—", AppTheme.INFO_COLOR);
    private final StatCard doctorCard = new StatCard("Total doctors", "—", AppTheme.SECONDARY_COLOR);
    private final StatCard appointmentCard = new StatCard("Appointments", "—", AppTheme.ACCENT_COLOR);
    private final StatCard pendingCard = new StatCard("Pending appointments", "—", AppTheme.WARNING_COLOR);

    public AdminDashboard() {
        super("Hospital Management System");
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

        root.add(new AppHeader("Hospital Management System", "Administrator", "ADMIN"), BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        setContentPane(root);
    }

    private AppSidebar createSidebar() {
        AppSidebar sidebar = new AppSidebar(this::navigate, this::logout);
        sidebar.addNavigationItem("Dashboard");
        sidebar.addNavigationItem("Patients");
        sidebar.addNavigationItem("Doctors");
        sidebar.addNavigationItem("Appointments");
        sidebar.setSelected("Dashboard");
        return sidebar;
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
        switch (page) {
            case "Dashboard" -> refreshStatistics();
            case "Patients" -> openModule("Patient Management", PatientPanel::new);
            case "Doctors" -> openModule("Doctor Management", DoctorPanel::new);
            case "Appointments" -> openModule("Appointment Management", AppointmentPanel::new);
            default -> { }
        }
    }

    private void refreshStatistics() {
        try {
            patientCard.setValue(String.valueOf(dashboardDAO.getTotalPatients()));
            doctorCard.setValue(String.valueOf(dashboardDAO.getTotalDoctors()));
            appointmentCard.setValue(String.valueOf(dashboardDAO.getTotalAppointments()));
            pendingCard.setValue(String.valueOf(dashboardDAO.getPendingAppointments()));
        } catch (RuntimeException exception) {
            showFriendlyError("Unable to load dashboard information. Verify that MySQL is running.");
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
            dispose();
            SwingUtilities.invokeLater(LoginFrame::new);
        }
    }

    public static void main(String[] args) {
        AppTheme.install();
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
