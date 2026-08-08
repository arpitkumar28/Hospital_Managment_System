package ui;

import dao.DashboardDAO;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private DashboardDAO dashboardDAO;

    public AdminDashboard() {

        dashboardDAO = new DashboardDAO();

        setTitle("Hospital Management System - Admin Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        // ================= MAIN PANEL =================

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.setBackground(
                new Color(245, 247, 250)
        );


        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(
                new Color(30, 55, 80)
        );

        header.setPreferredSize(
                new Dimension(1100, 80)
        );

        JLabel title = new JLabel(
                "  🏥 Hospital Management System"
        );

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        JLabel adminLabel = new JLabel(
                "ADMIN  |  Dashboard   "
        );

        adminLabel.setForeground(Color.WHITE);

        adminLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15
                )
        );

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                adminLabel,
                BorderLayout.EAST
        );


        // ================= CENTER PANEL =================

        JPanel centerPanel = new JPanel(
                new BorderLayout()
        );

        centerPanel.setBackground(
                new Color(245, 247, 250)
        );


        // ================= DASHBOARD TITLE =================

        JLabel dashboardTitle = new JLabel(
                "📊 ADMIN DASHBOARD"
        );

        dashboardTitle.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        dashboardTitle.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        15,
                        0
                )
        );

        centerPanel.add(
                dashboardTitle,
                BorderLayout.NORTH
        );


        // ================= CARDS =================

        JPanel cardsPanel = new JPanel(
                new GridLayout(
                        2,
                        3,
                        20,
                        20
                )
        );

        cardsPanel.setBackground(
                new Color(245, 247, 250)
        );

        cardsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        30,
                        20,
                        30
                )
        );


        // Patient count

        cardsPanel.add(
                createCard(
                        "👤 Total Patients",
                        String.valueOf(
                                dashboardDAO.getTotalPatients()
                        )
                )
        );


        // Doctor count

        cardsPanel.add(
                createCard(
                        "👨‍⚕️ Total Doctors",
                        String.valueOf(
                                dashboardDAO.getTotalDoctors()
                        )
                )
        );


        // Appointment count

        cardsPanel.add(
                createCard(
                        "📅 Appointments",
                        String.valueOf(
                                dashboardDAO.getTotalAppointments()
                        )
                )
        );


        // Admissions

        cardsPanel.add(
                createCard(
                        "🏥 Active Admissions",
                        String.valueOf(
                                dashboardDAO.getActiveAdmissions()
                        )
                )
        );


        // Beds

        cardsPanel.add(
                createCard(
                        "🛏️ Available Beds",
                        String.valueOf(
                                dashboardDAO.getAvailableBeds()
                        )
                )
        );


        // Revenue

        cardsPanel.add(
                createCard(
                        "💰 Total Revenue",
                        String.format(
                                "₹ %.2f",
                                dashboardDAO.getTotalRevenue()
                        )
                )
        );


        centerPanel.add(
                cardsPanel,
                BorderLayout.CENTER
        );


        // ================= PATIENT BUTTON =================

        JPanel menuPanel = new JPanel();

        menuPanel.setBackground(
                new Color(245, 247, 250)
        );

        JButton patientButton =
                new JButton(
                        "👤 Patient Management"
                );
        JButton doctorButton =
                new JButton("👨‍⚕️ Doctor Management");

        doctorButton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        doctorButton.setPreferredSize(
                new Dimension(230, 45)
        );

        doctorButton.addActionListener(
                e -> new DoctorPanel()
        );

        menuPanel.add(doctorButton);

        patientButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        16
                )
        );

        patientButton.setPreferredSize(
                new Dimension(230, 45)
        );


        patientButton.addActionListener(
                e -> new PatientPanel()
        );


        menuPanel.add(
                patientButton
        );


        centerPanel.add(
                menuPanel,
                BorderLayout.SOUTH
        );


        // ================= ADD PANELS =================

        mainPanel.add(
                header,
                BorderLayout.NORTH
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        add(mainPanel);
    }


    // =====================================================
    // CREATE DASHBOARD CARD
    // =====================================================

    private JPanel createCard(
            String title,
            String value) {

        JPanel card = new JPanel(
                new BorderLayout()
        );

        card.setBackground(
                Color.WHITE
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 220, 220)
                        ),

                        BorderFactory.createEmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );


        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        17
                )
        );


        JLabel valueLabel = new JLabel(
                value,
                SwingConstants.CENTER
        );

        valueLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        32
                )
        );

        valueLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        0,
                        5,
                        0
                )
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
}