package ui;

import dao.DashboardDAO;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    // =====================================================
    // COLORS
    // =====================================================

    private final Color PRIMARY =
            new Color(30, 55, 80);

    private final Color SECONDARY =
            new Color(45, 85, 120);

    private final Color BACKGROUND =
            new Color(245, 247, 250);

    private final Color WHITE =
            Color.WHITE;


    // =====================================================
    // DASHBOARD DAO
    // =====================================================

    private final DashboardDAO dashboardDAO;


    // =====================================================
    // STAT LABELS
    // =====================================================

    private JLabel patientCountLabel;
    private JLabel doctorCountLabel;
    private JLabel appointmentCountLabel;
    private JLabel pendingCountLabel;


    // =====================================================
    // CENTER PANEL
    // =====================================================

    private JPanel centerPanel;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdminDashboard() {

        dashboardDAO =
                new DashboardDAO();

        createUI();

        setTitle(
                "Hospital Management System - Admin Dashboard"
        );

        setSize(
                1200,
                750
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        loadDashboardStatistics();

        setVisible(true);
    }


    // =====================================================
    // CREATE UI
    // =====================================================

    private void createUI() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout()
                );

        mainPanel.setBackground(
                BACKGROUND
        );


        // =================================================
        // HEADER
        // =================================================

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(
                PRIMARY
        );

        headerPanel.setPreferredSize(
                new Dimension(
                        1200,
                        80
                )
        );


        JLabel titleLabel =
                new JLabel(
                        "  HOSPITAL MANAGEMENT SYSTEM"
                );

        titleLabel.setForeground(
                WHITE
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );


        JLabel adminLabel =
                new JLabel(
                        "ADMIN   "
                );

        adminLabel.setForeground(
                WHITE
        );

        adminLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );


        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        headerPanel.add(
                adminLabel,
                BorderLayout.EAST
        );


        // =================================================
        // SIDEBAR
        // =================================================

        JPanel sidePanel =
                new JPanel();

        sidePanel.setLayout(
                new BoxLayout(
                        sidePanel,
                        BoxLayout.Y_AXIS
                )
        );

        sidePanel.setBackground(
                SECONDARY
        );

        sidePanel.setPreferredSize(
                new Dimension(
                        230,
                        670
                )
        );


        JLabel menuTitle =
                new JLabel(
                        "  ADMIN MENU"
                );

        menuTitle.setForeground(
                WHITE
        );

        menuTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        menuTitle.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        15,
                        20,
                        10
                )
        );

        menuTitle.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );


        sidePanel.add(
                menuTitle
        );


        // =================================================
        // DASHBOARD
        // =================================================

        JButton dashboardButton =
                createMenuButton(
                        "Dashboard"
                );

        dashboardButton.addActionListener(
                e -> loadDashboard()
        );

        sidePanel.add(
                dashboardButton
        );


        // =================================================
        // PATIENTS
        // =================================================

        JButton patientButton =
                createMenuButton(
                        "Patients"
                );

        patientButton.addActionListener(
                e -> openPatientPanel()
        );

        sidePanel.add(
                patientButton
        );


        // =================================================
        // DOCTORS
        // =================================================

        JButton doctorButton =
                createMenuButton(
                        "Doctors"
                );

        doctorButton.addActionListener(
                e -> openDoctorPanel()
        );

        sidePanel.add(
                doctorButton
        );


        // =================================================
        // APPOINTMENTS
        // =================================================

        JButton appointmentButton =
                createMenuButton(
                        "Appointments"
                );

        appointmentButton.addActionListener(
                e -> openAppointmentPanel()
        );

        sidePanel.add(
                appointmentButton
        );


        sidePanel.add(
                Box.createVerticalGlue()
        );


        // =================================================
        // LOGOUT
        // =================================================

        JButton logoutButton =
                createMenuButton(
                        "Logout"
                );

        logoutButton.addActionListener(
                e -> logout()
        );

        sidePanel.add(
                logoutButton
        );


        // =================================================
        // CENTER
        // =================================================

        centerPanel =
                new JPanel(
                        new BorderLayout()
                );

        centerPanel.setBackground(
                BACKGROUND
        );


        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                sidePanel,
                BorderLayout.WEST
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        add(mainPanel);


        loadDashboard();
    }


    // =====================================================
    // LOAD DASHBOARD
    // =====================================================

    private void loadDashboard() {

        centerPanel.removeAll();


        // =================================================
        // TITLE
        // =================================================

        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new BoxLayout(
                        titlePanel,
                        BoxLayout.Y_AXIS
                )
        );

        titlePanel.setBackground(
                BACKGROUND
        );

        titlePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        10,
                        30
                )
        );


        JLabel title =
                new JLabel(
                        "Admin Dashboard"
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        title.setForeground(
                PRIMARY
        );


        JLabel subtitle =
                new JLabel(
                        "Hospital overview and statistics"
                );

        subtitle.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15
                )
        );

        subtitle.setForeground(
                Color.GRAY
        );


        titlePanel.add(title);

        titlePanel.add(
                Box.createVerticalStrut(5)
        );

        titlePanel.add(subtitle);


        // =================================================
        // STATISTICS
        // =================================================

        JPanel cardsPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                20,
                                20
                        )
                );

        cardsPanel.setBackground(
                BACKGROUND
        );

        cardsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        30,
                        30,
                        30
                )
        );


        // Patient card

        JPanel patientCard =
                createStatCard(
                        "PATIENTS",
                        "0"
                );

        patientCountLabel =
                findCountLabel(
                        patientCard
                );


        // Doctor card

        JPanel doctorCard =
                createStatCard(
                        "DOCTORS",
                        "0"
                );

        doctorCountLabel =
                findCountLabel(
                        doctorCard
                );


        // Appointment card

        JPanel appointmentCard =
                createStatCard(
                        "APPOINTMENTS",
                        "0"
                );

        appointmentCountLabel =
                findCountLabel(
                        appointmentCard
                );


        // Pending card

        JPanel pendingCard =
                createStatCard(
                        "PENDING APPOINTMENTS",
                        "0"
                );

        pendingCountLabel =
                findCountLabel(
                        pendingCard
                );


        cardsPanel.add(
                patientCard
        );

        cardsPanel.add(
                doctorCard
        );

        cardsPanel.add(
                appointmentCard
        );

        cardsPanel.add(
                pendingCard
        );


        centerPanel.add(
                titlePanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                cardsPanel,
                BorderLayout.CENTER
        );


        centerPanel.revalidate();

        centerPanel.repaint();


        loadDashboardStatistics();
    }


    // =====================================================
    // CREATE STAT CARD
    // =====================================================

    private JPanel createStatCard(
            String title,
            String value) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(
                WHITE
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        220,
                                        225,
                                        230
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                25,
                                25,
                                25,
                                25
                        )
                )
        );


        JLabel titleLabel =
                new JLabel(
                        title
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        titleLabel.setForeground(
                PRIMARY
        );


        JLabel valueLabel =
                new JLabel(
                        value
                );

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        42
                )
        );

        valueLabel.setForeground(
                SECONDARY
        );

        valueLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        card.add(
                titleLabel,
                BorderLayout.NORTH
        );

        card.add(
                valueLabel,
                BorderLayout.CENTER
        );


        return card;
    }


    // =====================================================
    // FIND COUNT LABEL
    // =====================================================

    private JLabel findCountLabel(
            JPanel panel) {

        for (
                Component component
                : panel.getComponents()
        ) {

            if (
                    component instanceof JLabel
                    && ((JLabel) component)
                    .getFont()
                    .getSize() >= 40
            ) {

                return (JLabel) component;
            }
        }

        return null;
    }


    // =====================================================
    // LOAD STATISTICS FROM DATABASE
    // =====================================================

    private void loadDashboardStatistics() {

        int patients =
                dashboardDAO
                        .getTotalPatients();

        int doctors =
                dashboardDAO
                        .getTotalDoctors();

        int appointments =
                dashboardDAO
                        .getTotalAppointments();

        int pending =
                dashboardDAO
                        .getPendingAppointments();


        if (patientCountLabel != null) {

            patientCountLabel.setText(
                    String.valueOf(patients)
            );
        }


        if (doctorCountLabel != null) {

            doctorCountLabel.setText(
                    String.valueOf(doctors)
            );
        }


        if (appointmentCountLabel != null) {

            appointmentCountLabel.setText(
                    String.valueOf(appointments)
            );
        }


        if (pendingCountLabel != null) {

            pendingCountLabel.setText(
                    String.valueOf(pending)
            );
        }
    }


    // =====================================================
    // MENU BUTTON
    // =====================================================

    private JButton createMenuButton(
            String text) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        button.setForeground(
                WHITE
        );

        button.setBackground(
                SECONDARY
        );

        button.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        button.setFocusPainted(
                false
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        20,
                        14,
                        10
                )
        );

        button.setMaximumSize(
                new Dimension(
                        230,
                        55
                )
        );

        button.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return button;
    }


    // =====================================================
    // PATIENT PANEL
    // =====================================================

    private void openPatientPanel() {

        try {

            new PatientPanel();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open Patient Management.\n\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // DOCTOR PANEL
    // =====================================================

    private void openDoctorPanel() {

        try {

            new DoctorPanel();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open Doctor Management.\n\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // APPOINTMENT PANEL
    // =====================================================

    private void openAppointmentPanel() {

        try {

            new AppointmentPanel();

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open Appointment Management.\n\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // LOGOUT
    // =====================================================

    private void logout() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );


        if (
                result != JOptionPane.YES_OPTION
        ) {

            return;
        }


        dispose();


        SwingUtilities.invokeLater(
                () -> {

                    try {

                        new LoginFrame();

                    } catch (Exception e) {

                        e.printStackTrace();

                        JOptionPane.showMessageDialog(
                                null,
                                "Unable to open Login screen.\n\n"
                                        + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
        );
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                () -> new AdminDashboard()
        );
    }
}