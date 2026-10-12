package ui;

import service.AdmissionService;
import ui.theme.AppTheme;

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

    private AdmissionService admissionService;

    private int selectedAdmissionId = -1;
    private ui.components.SearchableTablePanel searchableTable;
    private boolean refreshingData;
    private boolean refreshPending;
    private JButton dischargeButton;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdmissionPanel() {

        admissionService =
                new AdmissionService();

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
        AppTheme.styleLegacyWindow(getContentPane());

        refreshData();

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
                        280,
                        38
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
                        320,
                        38
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
                        220,
                        38
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

        dischargeButton =
                new JButton(
                        "Discharge Patient"
                );
        dischargeButton.setEnabled(false);

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

        searchableTable = ui.components.SearchableTablePanel.wrap(admissionTable, scrollPane,
                "Search by patient, bed, or room", this::refreshData, 1, 2, 3);
        tablePanel.add(searchableTable, BorderLayout.CENTER);


        mainPanel.add(
                tablePanel,
                BorderLayout.CENTER
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


        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(top, BorderLayout.NORTH);
        setMinimumSize(new Dimension(900, 620));
        scrollPane.setPreferredSize(new Dimension(0, 340));
        admissionTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {110, 220, 120, 120, 160, 160};
        for (int column = 0; column < widths.length; column++) {
            admissionTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }
        add(mainPanel);
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


        int patientId = patient.patientId;
        int bedId = bed.bedId;
        runAdmissionMutation("Admitting patient…", "Patient admitted successfully.",
                "Unable to admit patient. The bed may no longer be available.",
                () -> admissionService.admitPatient(patientId, bedId, admissionDate));
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


        int admissionId = selectedAdmissionId;
        Date finalDischargeDate = Date.valueOf(LocalDate.now());
        runAdmissionMutation("Discharging patient…", "Patient discharged successfully.",
                "Unable to discharge patient.",
                () -> admissionService.dischargePatient(admissionId, finalDischargeDate));
    }

    private void runAdmissionMutation(String progress, String successMessage, String failureMessage,
                                      java.util.concurrent.Callable<Boolean> operation) {
        searchableTable.setLoading(progress);
        ui.components.SwingTaskRunner.run(getContentPane(), operation, success -> {
            if (success) {
                JOptionPane.showMessageDialog(this, successMessage, "Admission Management",
                        JOptionPane.INFORMATION_MESSAGE);
                searchableTable.setLoaded();
                clearFields();
                refreshData();
            } else {
                searchableTable.setError(failureMessage);
                JOptionPane.showMessageDialog(this, failureMessage, "Admission Management",
                        JOptionPane.ERROR_MESSAGE);
            }
        }, exception -> {
            String message = "Unable to complete the admission operation. Check the database connection and retry.";
            searchableTable.setError(message);
            JOptionPane.showMessageDialog(this, message, "Admission Management", JOptionPane.ERROR_MESSAGE);
        });
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
            dischargeButton.setEnabled(false);
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
        Object dischargedAt = admissionTable.getValueAt(row, 5);
        dischargeButton.setEnabled(dischargedAt == null || dischargedAt.toString().isBlank()
                || "null".equalsIgnoreCase(dischargedAt.toString()));
    }


    // =====================================================
    // REFRESH
    // =====================================================

    private void refreshData() {
        if (refreshingData) {
            refreshPending = true;
            return;
        }
        refreshingData = true;
        searchableTable.setLoading("Loading patients, available beds, and admissions…");
        new SwingWorker<AdmissionData, Void>() {
            @Override protected AdmissionData doInBackground() {
                return new AdmissionData(admissionService.getPatients(),
                        admissionService.getAvailableBeds(), admissionService.getAllAdmissions());
            }

            @Override protected void done() {
                refreshingData = false;
                try {
                    AdmissionData data = get();
                    patientComboBox.removeAllItems();
                    for (Object[] patient : data.patients()) {
                        patientComboBox.addItem(new PatientItem(Integer.parseInt(patient[0].toString()),
                                patient[1].toString()));
                    }
                    bedComboBox.removeAllItems();
                    for (Object[] bed : data.beds()) {
                        bedComboBox.addItem(new BedItem(Integer.parseInt(bed[0].toString()),
                                bed[1].toString(), bed[2].toString(), bed[3].toString()));
                    }
                    tableModel.setRowCount(0);
                    for (Object[] admission : data.admissions()) tableModel.addRow(admission);
                    searchableTable.setLoaded();
                } catch (Exception exception) {
                    searchableTable.setError("Could not refresh admissions. Check the database connection and retry.");
                } finally {
                    if (refreshPending) {
                        refreshPending = false;
                        refreshData();
                    }
                }
            }
        }.execute();
    }

    private record AdmissionData(List<Object[]> patients, List<Object[]> beds, List<Object[]> admissions) { }


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
