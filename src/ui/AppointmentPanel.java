package ui;

import service.AppointmentService;
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
                new Dimension(220, 30)
        );


        // Doctor

        doctorBox =
                new JComboBox<>();

        doctorBox.setPreferredSize(
                new Dimension(220, 30)
        );


        // Date

        dateField =
                new JTextField();

        dateField.setPreferredSize(
                new Dimension(220, 30)
        );


        // Time

        timeField =
                new JTextField();

        timeField.setPreferredSize(
                new Dimension(220, 30)
        );


        // Reason

        reasonField =
                new JTextField();

        reasonField.setPreferredSize(
                new Dimension(220, 30)
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
                new Dimension(220, 30)
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
                        e -> selectAppointment()
                );


        // =================================================
        // ADD TO MAIN PANEL
        // =================================================

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                tableScrollPane,
                BorderLayout.SOUTH
        );


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

        panel.add(
                new JLabel(label),
                gbc
        );

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
        patientBox.removeAllItems();
        try {
            for (Object[] patient : appointmentService.getPatients()) {
                patientBox.addItem(new PatientItem((Integer) patient[0], (String) patient[1]));
            }
        } catch (RuntimeException exception) {
            LOGGER.error("Unable to load appointment patients.", exception);
            JOptionPane.showMessageDialog(this,
                    "Unable to load patients. Verify the database connection and try again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // LOAD DOCTORS
    // =====================================================

    private void loadDoctors() {
        doctorBox.removeAllItems();
        try {
            for (Object[] doctor : appointmentService.getDoctors()) {
                doctorBox.addItem(new DoctorItem((Integer) doctor[0], (String) doctor[1], (String) doctor[2]));
            }
        } catch (RuntimeException exception) {
            LOGGER.error("Unable to load appointment doctors.", exception);
            JOptionPane.showMessageDialog(this,
                    "Unable to load doctors. Verify the database connection and try again.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // =====================================================
    // LOAD APPOINTMENTS
    // =====================================================

    private void loadAppointments() {

        tableModel.setRowCount(0);

        for (
                Object[] appointment
                : appointmentService.getAllAppointments()
        ) {

            tableModel.addRow(appointment);
        }
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


        boolean success =
                appointmentService.addAppointment(
                        patient.id,
                        doctor.id,
                        dateField.getText().trim(),
                        timeField.getText().trim(),
                        reasonField.getText().trim(),
                        statusBox
                                .getSelectedItem()
                                .toString()
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment booked successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadAppointments();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to book appointment.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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


        boolean success =
                appointmentService.updateAppointment(
                        selectedAppointmentId,
                        patient.id,
                        doctor.id,
                        dateField.getText().trim(),
                        timeField.getText().trim(),
                        reasonField.getText().trim(),
                        statusBox
                                .getSelectedItem()
                                .toString()
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadAppointments();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to update appointment.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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


        boolean success =
                appointmentService.deleteAppointment(
                        selectedAppointmentId
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Appointment deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();

            loadAppointments();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to delete appointment.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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
