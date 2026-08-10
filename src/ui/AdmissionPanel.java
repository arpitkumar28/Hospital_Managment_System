package ui;

import dao.AdmissionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AdmissionPanel extends JFrame {

    private JComboBox<PatientItem> patientComboBox;

    private JComboBox<BedItem> bedComboBox;

    private JTextField admissionDateField;

    private JTable admissionTable;

    private DefaultTableModel tableModel;

    private AdmissionDAO admissionDAO;

    private int selectedAdmissionId = -1;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdmissionPanel() {

        admissionDAO =
                new AdmissionDAO();

        setTitle(
                "Hospital Management System - Admission Management"
        );

        setSize(
                1050,
                700
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        createUI();

        loadPatients();

        loadBeds();

        loadAdmissions();

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
                new Color(
                        245,
                        247,
                        250
                )
        );


        // =================================================
        // HEADER
        // =================================================

        JLabel title =
                new JLabel(
                        "Admission Management",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        28
                )
        );

        title.setForeground(
                new Color(
                        30,
                        55,
                        80
                )
        );

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        10,
                        20,
                        10
                )
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );


        // =================================================
        // FORM
        // =================================================

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(
                Color.WHITE
        );

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        20,
                        15,
                        20
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        8,
                        8,
                        8,
                        8
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        // =================================================
        // PATIENT
        // =================================================

        gbc.gridx = 0;
        gbc.gridy = 0;

        formPanel.add(
                new JLabel("Patient:"),
                gbc
        );


        patientComboBox =
                new JComboBox<>();

        patientComboBox.setPreferredSize(
                new Dimension(
                        300,
                        30
                )
        );

        gbc.gridx = 1;

        formPanel.add(
                patientComboBox,
                gbc
        );


        // =================================================
        // BED
        // =================================================

        gbc.gridx = 2;

        formPanel.add(
                new JLabel("Available Bed:"),
                gbc
        );


        bedComboBox =
                new JComboBox<>();

        bedComboBox.setPreferredSize(
                new Dimension(
                        300,
                        30
                )
        );

        gbc.gridx = 3;

        formPanel.add(
                bedComboBox,
                gbc
        );


        // =================================================
        // ADMISSION DATE
        // =================================================

        gbc.gridx = 0;
        gbc.gridy = 1;

        formPanel.add(
                new JLabel(
                        "Admission Date:"
                ),
                gbc
        );


        admissionDateField =
                new JTextField(
                        LocalDate.now()
                                .toString()
                );

        admissionDateField.setPreferredSize(
                new Dimension(
                        300,
                        30
                )
        );

        gbc.gridx = 1;

        formPanel.add(
                admissionDateField,
                gbc
        );


        // =================================================
        // BUTTONS
        // =================================================

        JButton admitButton =
                new JButton(
                        "Admit Patient"
                );

        JButton dischargeButton =
                new JButton(
                        "Discharge Patient"
                );

        JButton refreshButton =
                new JButton(
                        "Refresh"
                );

        JButton clearButton =
                new JButton(
                        "Clear"
                );


        gbc.gridx = 0;
        gbc.gridy = 2;

        formPanel.add(
                admitButton,
                gbc
        );


        gbc.gridx = 1;

        formPanel.add(
                dischargeButton,
                gbc
        );


        gbc.gridx = 2;

        formPanel.add(
                refreshButton,
                gbc
        );


        gbc.gridx = 3;

        formPanel.add(
                clearButton,
                gbc
        );


        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );


        // =================================================
        // TABLE
        // =================================================

        tableModel =
                new DefaultTableModel(
                        new String[]{
                                "Admission ID",
                                "Patient",
                                "Bed",
                                "Room",
                                "Admission Date",
                                "Discharge Date"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };


        admissionTable =
                new JTable(
                        tableModel
                );

        admissionTable.setRowHeight(
                30
        );

        admissionTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        admissionTable
                );

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Admission History"
                )
        );


        JPanel tablePanel =
                new JPanel(
                        new BorderLayout()
                );

        tablePanel.setBackground(
                new Color(
                        245,
                        247,
                        250
                )
        );

        tablePanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        20,
                        20,
                        20
                )
        );

        tablePanel.add(
                scrollPane,
                BorderLayout.CENTER
        );


        mainPanel.add(
                tablePanel,
                BorderLayout.SOUTH
        );


        // =================================================
        // EVENTS
        // =================================================

        admitButton.addActionListener(
                e -> admitPatient()
        );


        dischargeButton.addActionListener(
                e -> dischargePatient()
        );


        refreshButton.addActionListener(
                e -> refreshData()
        );


        clearButton.addActionListener(
                e -> clearFields()
        );


        admissionTable
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {

                            if (
                                    !e.getValueIsAdjusting()
                            ) {

                                selectAdmission();
                            }
                        }
                );


        add(mainPanel);
    }


    // =====================================================
    // LOAD PATIENTS
    // =====================================================

    private void loadPatients() {

        patientComboBox.removeAllItems();

        List<Object[]> patients =
                admissionDAO.getPatients();


        for (Object[] patient : patients) {

            patientComboBox.addItem(
                    new PatientItem(
                            Integer.parseInt(
                                    patient[0]
                                            .toString()
                            ),
                            patient[1]
                                    .toString()
                    )
            );
        }
    }


    // =====================================================
    // LOAD BEDS
    // =====================================================

    private void loadBeds() {

        bedComboBox.removeAllItems();

        List<Object[]> beds =
                admissionDAO.getAvailableBeds();


        for (Object[] bed : beds) {

            bedComboBox.addItem(
                    new BedItem(
                            Integer.parseInt(
                                    bed[0]
                                            .toString()
                            ),
                            bed[1]
                                    .toString(),
                            bed[2]
                                    .toString(),
                            bed[3]
                                    .toString()
                    )
            );
        }
    }


    // =====================================================
    // LOAD ADMISSIONS
    // =====================================================

    private void loadAdmissions() {

        tableModel.setRowCount(0);

        List<Object[]> admissions =
                admissionDAO.getAllAdmissions();


        for (Object[] admission :
                admissions) {

            tableModel.addRow(
                    admission
            );
        }
    }


    // =====================================================
    // ADMIT PATIENT
    // =====================================================

    private void admitPatient() {

        PatientItem patient =
                (PatientItem)
                        patientComboBox
                                .getSelectedItem();


        BedItem bed =
                (BedItem)
                        bedComboBox
                                .getSelectedItem();


        String dateText =
                admissionDateField
                        .getText()
                        .trim();


        if (patient == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a patient.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (bed == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "No available bed selected.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        Date admissionDate;

        try {

            admissionDate =
                    Date.valueOf(
                            dateText
                    );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Date must be in YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        boolean success =
                admissionDAO.admitPatient(
                        patient.patientId,
                        bed.bedId,
                        admissionDate
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Patient admitted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            refreshData();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to admit patient.\n"
                            + "The bed may no longer be available.",
                    "Admission Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // DISCHARGE PATIENT
    // =====================================================

    private void dischargePatient() {

        if (
                selectedAdmissionId == -1
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an active admission.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        int row =
                admissionTable
                        .getSelectedRow();


        if (row == -1) {

            return;
        }


        Object dischargeDate =
                admissionTable
                        .getValueAt(
                                row,
                                5
                        );


        if (
                dischargeDate != null
                        &&
                        !dischargeDate
                                .toString()
                                .equals("null")
                        &&
                        !dischargeDate
                                .toString()
                                .isEmpty()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "This patient has already been discharged.",
                    "Already Discharged",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }


        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Discharge this patient today?",
                        "Confirm Discharge",
                        JOptionPane.YES_NO_OPTION
                );


        if (
                confirmation
                        != JOptionPane.YES_OPTION
        ) {

            return;
        }


        boolean success =
                admissionDAO.dischargePatient(
                        selectedAdmissionId,
                        Date.valueOf(
                                LocalDate.now()
                        )
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Patient discharged successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            refreshData();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to discharge patient.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // SELECT ADMISSION
    // =====================================================

    private void selectAdmission() {

        int row =
                admissionTable
                        .getSelectedRow();


        if (row == -1) {

            selectedAdmissionId = -1;

            return;
        }


        selectedAdmissionId =
                Integer.parseInt(
                        admissionTable
                                .getValueAt(
                                        row,
                                        0
                                )
                                .toString()
                );
    }


    // =====================================================
    // REFRESH
    // =====================================================

    private void refreshData() {

        loadPatients();

        loadBeds();

        loadAdmissions();
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        selectedAdmissionId = -1;

        admissionTable.clearSelection();

        admissionDateField.setText(
                LocalDate.now().toString()
        );

        if (
                patientComboBox
                        .getItemCount() > 0
        ) {

            patientComboBox
                    .setSelectedIndex(0);
        }

        if (
                bedComboBox
                        .getItemCount() > 0
        ) {

            bedComboBox
                    .setSelectedIndex(0);
        }
    }


    // =====================================================
    // PATIENT ITEM
    // =====================================================

    private static class PatientItem {

        int patientId;

        String name;


        PatientItem(
                int patientId,
                String name) {

            this.patientId =
                    patientId;

            this.name =
                    name;
        }


        @Override
        public String toString() {

            return patientId
                    + " - "
                    + name;
        }
    }


    // =====================================================
    // BED ITEM
    // =====================================================

    private static class BedItem {

        int bedId;

        String bedNumber;

        String roomNumber;

        String roomType;


        BedItem(
                int bedId,
                String bedNumber,
                String roomNumber,
                String roomType) {

            this.bedId =
                    bedId;

            this.bedNumber =
                    bedNumber;

            this.roomNumber =
                    roomNumber;

            this.roomType =
                    roomType;
        }


        @Override
        public String toString() {

            return "Room "
                    + roomNumber
                    + " | Bed "
                    + bedNumber
                    + " | "
                    + roomType;
        }
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(
                AdmissionPanel::new
        );
    }
}