package ui;

import service.AppointmentService;
import ui.theme.AppTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;

public class AppointmentPanel extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentPanel.class);

    private JComboBox<PatientItem> patientBox;
    private JComboBox<DoctorItem> doctorBox;

    private JTextField dateField;
    private JTextField timeField;
    private JTextField reasonField;

    private JComboBox<String> statusBox;

    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    private final AppointmentService appointmentService;

    private int selectedAppointmentId = -1;
    private ui.components.SearchableTablePanel searchableTable;
    private boolean loadingPatients;
    private boolean loadingDoctors;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AppointmentPanel() {

        appointmentService = new AppointmentService();

        setTitle("Hospital Management System - Appointment Management");

        setSize(1200, 750);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLocationRelativeTo(null);

        createUI();
        AppTheme.styleLegacyWindow(getContentPane());

        loadPatients();
        loadDoctors();
        loadAppointments();

        setVisible(true);
    }


    // =====================================================
    // CREATE UI
    // =====================================================

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.setBackground(new Color(245, 247, 250));


        // =================================================
        // HEADER
        // =================================================

        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(new Color(30, 55, 80));

        headerPanel.setPreferredSize(
                new Dimension(1200, 70)
        );

        JLabel titleLabel =
                new JLabel("  APPOINTMENT MANAGEMENT");

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );


        // =================================================
        // FORM
        // =================================================

        JPanel formPanel =
                new JPanel(new GridBagLayout());

        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Appointment Information"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 10, 8, 10);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        // Patient

        patientBox =
                new JComboBox<>();

        patientBox.setPreferredSize(
                new Dimension(280, 38)
        );


        // Doctor

        doctorBox =
                new JComboBox<>();

        doctorBox.setPreferredSize(
                new Dimension(280, 38)
        );


        // Date

        dateField =
                new JTextField();

        dateField.setPreferredSize(
                new Dimension(220, 38)
        );


        // Time

        timeField =
                new JTextField();

        timeField.setPreferredSize(
                new Dimension(180, 38)
        );


        // Reason

        reasonField =
                new JTextField();

        reasonField.setPreferredSize(
                new Dimension(260, 38)
        );


        // Status

        statusBox =
                new JComboBox<>(
                        new String[]{
                                "PENDING",
                                "CONFIRMED",
                                "COMPLETED",
                                "CANCELLED"
                        }
                );

        statusBox.setPreferredSize(
                new Dimension(220, 38)
        );


        // Row 1

        addField(
                formPanel,
                gbc,
                0,
                0,
                "Patient:",
                patientBox
        );

        addField(
                formPanel,
                gbc,
                2,
                0,
                "Doctor:",
                doctorBox
        );


        // Row 2

        addField(
                formPanel,
                gbc,
                0,
                1,
                "Date:",
                dateField
        );

        addField(
                formPanel,
                gbc,
                2,
                1,
                "Time:",
                timeField
        );


        // Row 3

        addField(
                formPanel,
                gbc,
                0,
                2,
                "Reason:",
                reasonField
        );

        addField(
                formPanel,
                gbc,
                2,
                2,
                "Status:",
                statusBox
        );


        // =================================================
        // BUTTONS
        // =================================================

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.setBackground(Color.WHITE);


        JButton addButton =
                new JButton("Book Appointment");

        JButton updateButton =
                new JButton("Update");

        JButton deleteButton =
                new JButton("Delete");

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        JButton clearButton =
                new JButton("Clear");


        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);


        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;

        formPanel.add(
                buttonPanel,
                gbc
        );


        // =================================================
        // TABLE
        // =================================================

        String[] columns = {
                "ID",
                "Patient",
                "Doctor",
                "Specialization",
                "Date",
                "Time",
                "Reason",
                "Status"
        };


        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };


        appointmentTable =
                new JTable(tableModel);

        appointmentTable.setRowHeight(28);

        appointmentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        appointmentTable
                .getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );


        JScrollPane tableScrollPane =
                new JScrollPane(
                        appointmentTable
                );

        tableScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Appointment Records"
                )
        );


        // =================================================
        // BUTTON EVENTS
        // =================================================

        addButton.addActionListener(
                e -> addAppointment()
        );

        updateButton.addActionListener(
                e -> updateAppointment()
        );

        deleteButton.addActionListener(
                e -> deleteAppointment()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );


        appointmentTable
                .getSelectionModel()
                .addListSelectionListener(
                        e -> {
                            if (e.getValueIsAdjusting()) return;
                            boolean selected = appointmentTable.getSelectedRow() >= 0;
                            updateButton.setEnabled(selected);
                            deleteButton.setEnabled(selected);
                            selectAppointment();
                        }
                );


        // =================================================
        // ADD TO MAIN PANEL
        // =================================================

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(headerPanel, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(top, BorderLayout.NORTH);
        searchableTable = ui.components.SearchableTablePanel.wrap(appointmentTable, tableScrollPane,
                "Search by patient, doctor, reason, or status", this::refreshData, 1, 2, 6, 7);
        mainPanel.add(searchableTable, BorderLayout.CENTER);

        setMinimumSize(new Dimension(900, 620));
        tableScrollPane.setPreferredSize(new Dimension(0, 360));
        appointmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {55, 170, 170, 175, 115, 100, 260, 120};
        for (int column = 0; column < widths.length; column++) {
            appointmentTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }


        add(mainPanel);
    }


    // =====================================================
    // ADD FORM FIELD
    // =====================================================

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int x,
            int y,
            String label,
            JComponent component) {

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = 1;

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setLabelFor(component);
        panel.add(fieldLabel, gbc);

        gbc.gridx = x + 1;

        panel.add(
                component,
                gbc
        );
    }


    // =====================================================
    // LOAD PATIENTS
    // =====================================================

    private void loadPatients() {
        if (loadingPatients) return;
        loadingPatients = true;
        new SwingWorker<java.util.List<Object[]>, Void>() {
            @Override protected java.util.List<Object[]> doInBackground() {
                return appointmentService.getPatients();
            }
            @Override protected void done() {
                loadingPatients = false;
                try {
                    patientBox.removeAllItems();
                    for (Object[] patient : get()) {
                        patientBox.addItem(new PatientItem((Integer) patient[0], (String) patient[1]));
                    }
                } catch (Exception exception) {
                    LOGGER.error("Unable to load appointment patients.", exception);
                    JOptionPane.showMessageDialog(AppointmentPanel.this,
                            "Unable to load patients. Check the database connection and retry.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    // =====================================================
    // LOAD DOCTORS
    // =====================================================

    private void loadDoctors() {
        if (loadingDoctors) return;
        loadingDoctors = true;
        new SwingWorker<java.util.List<Object[]>, Void>() {
            @Override protected java.util.List<Object[]> doInBackground() {
                return appointmentService.getDoctors();
            }
            @Override protected void done() {
                loadingDoctors = false;
                try {
                    doctorBox.removeAllItems();
                    for (Object[] doctor : get()) {
                        doctorBox.addItem(new DoctorItem((Integer) doctor[0],
                                (String) doctor[1], (String) doctor[2]));
                    }
                } catch (Exception exception) {
                    LOGGER.error("Unable to load appointment doctors.", exception);
                    JOptionPane.showMessageDialog(AppointmentPanel.this,
                            "Unable to load doctors. Check the database connection and retry.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    // =====================================================
    // LOAD APPOINTMENTS
    // =====================================================

    private void loadAppointments() {
        searchableTable.loadAsync("Loading appointments…",
                "Could not load appointments. Check the database connection and retry.",
                appointmentService::getAllAppointments, appointments -> {
                    tableModel.setRowCount(0);
                    for (Object[] appointment : appointments) tableModel.addRow(appointment);
                });
    }

    private void refreshData() {
        loadPatients();
        loadDoctors();
        loadAppointments();
    }


    // =====================================================
    // ADD APPOINTMENT
    // =====================================================

    private void addAppointment() {

        if (!validateFields()) {
            return;
        }

        PatientItem patient =
                (PatientItem)
                        patientBox.getSelectedItem();

        DoctorItem doctor =
                (DoctorItem)
                        doctorBox.getSelectedItem();


        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String reason = reasonField.getText().trim();
        String status = statusBox.getSelectedItem().toString();
        runAppointmentMutation("Booking appointment…", "Appointment booked successfully.",
                "Could not book appointment.", () -> appointmentService.addAppointment(
                        patient.id, doctor.id, date, time, reason, status));
    }


    // =====================================================
    // UPDATE APPOINTMENT
    // =====================================================

    private void updateAppointment() {

        if (selectedAppointmentId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an appointment first."
            );

            return;
        }

        if (!validateFields()) {
            return;
        }


        PatientItem patient =
                (PatientItem)
                        patientBox.getSelectedItem();

        DoctorItem doctor =
                (DoctorItem)
                        doctorBox.getSelectedItem();


        int appointmentId = selectedAppointmentId;
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String reason = reasonField.getText().trim();
        String status = statusBox.getSelectedItem().toString();
        runAppointmentMutation("Updating appointment…", "Appointment updated successfully.",
                "Could not update appointment.", () -> appointmentService.updateAppointment(
                        appointmentId, patient.id, doctor.id, date, time, reason, status));
    }


    // =====================================================
    // DELETE APPOINTMENT
    // =====================================================

    private void deleteAppointment() {

        if (selectedAppointmentId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an appointment first."
            );

            return;
        }


        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "Delete this appointment?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );


        if (result != JOptionPane.YES_OPTION) {
            return;
        }


        int appointmentId = selectedAppointmentId;
        runAppointmentMutation("Deleting appointment…", "Appointment deleted successfully.",
                "Could not delete appointment.", () -> appointmentService.deleteAppointment(appointmentId));
    }

    private void runAppointmentMutation(String progress, String successMessage, String failureMessage,
                                        java.util.concurrent.Callable<Boolean> operation) {
        searchableTable.setLoading(progress);
        ui.components.SwingTaskRunner.run(getContentPane(), operation, success -> {
            if (success) {
                JOptionPane.showMessageDialog(this, successMessage, "Appointment Management",
                        JOptionPane.INFORMATION_MESSAGE);
                searchableTable.setLoaded();
                clearFields();
                loadAppointments();
            } else {
                searchableTable.setError(failureMessage);
                JOptionPane.showMessageDialog(this, failureMessage, "Appointment Management",
                        JOptionPane.ERROR_MESSAGE);
            }
        }, exception -> {
            String message = "Unable to complete the appointment operation. Check the database connection and retry.";
            searchableTable.setError(message);
            JOptionPane.showMessageDialog(this, message, "Appointment Management", JOptionPane.ERROR_MESSAGE);
        });
    }


    // =====================================================
    // SELECT APPOINTMENT FROM TABLE
    // =====================================================

    private void selectAppointment() {

        int row =
                appointmentTable.getSelectedRow();

        if (row == -1) {
            return;
        }


        selectedAppointmentId =
                Integer.parseInt(
                        appointmentTable
                                .getValueAt(row, 0)
                                .toString()
                );


        String patientName =
                appointmentTable
                        .getValueAt(row, 1)
                        .toString();


        String doctorName =
                appointmentTable
                        .getValueAt(row, 2)
                        .toString();


        // Select patient

        for (
                int i = 0;
                i < patientBox.getItemCount();
                i++
        ) {

            PatientItem patient =
                    patientBox.getItemAt(i);

            if (
                    patient.name.equals(
                            patientName
                    )
            ) {

                patientBox.setSelectedIndex(i);

                break;
            }
        }


        // Select doctor

        for (
                int i = 0;
                i < doctorBox.getItemCount();
                i++
        ) {

            DoctorItem doctor =
                    doctorBox.getItemAt(i);

            if (
                    doctor.name.equals(
                            doctorName
                    )
            ) {

                doctorBox.setSelectedIndex(i);

                break;
            }
        }


        dateField.setText(
                getTableValue(row, 4)
        );

        timeField.setText(
                getTableValue(row, 5)
        );

        reasonField.setText(
                getTableValue(row, 6)
        );

        statusBox.setSelectedItem(
                getTableValue(row, 7)
        );
    }


    // =====================================================
    // GET TABLE VALUE
    // =====================================================

    private String getTableValue(
            int row,
            int column) {

        Object value =
                appointmentTable.getValueAt(
                        row,
                        column
                );

        return value == null
                ? ""
                : value.toString();
    }


    // =====================================================
    // VALIDATION
    // =====================================================

    private boolean validateFields() {

        if (patientBox.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a patient."
            );

            return false;
        }


        if (doctorBox.getSelectedItem() == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a doctor."
            );

            return false;
        }


        String date =
                dateField.getText().trim();

        String time =
                timeField.getText().trim();

        String reason =
                reasonField.getText().trim();


        // Date validation

        try {

            Date.valueOf(date);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Date must be in:\nYYYY-MM-DD\n\nExample:\n2026-08-10",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }


        // Time validation

        try {

            Time.valueOf(time);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Time must be in:\nHH:MM:SS\n\nExample:\n10:30:00",
                    "Invalid Time",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }


        if (reason.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the appointment reason."
            );

            return false;
        }


        return true;
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        selectedAppointmentId = -1;

        dateField.setText("");
        timeField.setText("");
        reasonField.setText("");

        statusBox.setSelectedItem("PENDING");

        appointmentTable.clearSelection();
    }


    // =====================================================
    // PATIENT ITEM
    // =====================================================

    private static class PatientItem {

        private final int id;
        private final String name;


        public PatientItem(
                int id,
                String name) {

            this.id = id;
            this.name = name;
        }


        @Override
        public String toString() {

            return id + " - " + name;
        }
    }


    // =====================================================
    // DOCTOR ITEM
    // =====================================================

    private static class DoctorItem {

        private final int id;
        private final String name;
        private final String specialization;


        public DoctorItem(
                int id,
                String name,
                String specialization) {

            this.id = id;
            this.name = name;
            this.specialization =
                    specialization;
        }


        @Override
        public String toString() {

            return id
                    + " - "
                    + name
                    + " ("
                    + specialization
                    + ")";
        }
    }
}
