package ui;

import service.PatientService;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.SwingWorker;

public class PatientPanel extends JFrame {

    private JTextField nameField;
    private JComboBox<String> genderBox;
    private JTextField dobField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JComboBox<String> bloodGroupBox;
    private JTextField emergencyField;

    private JTable patientTable;
    private DefaultTableModel tableModel;

    private PatientService patientService;

    private int selectedPatientId = -1;
    private ui.components.SearchableTablePanel searchableTable;


    public PatientPanel() {

        patientService = new PatientService();

        setTitle("Hospital Management System - Patient Management");
        setSize(1250, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
        AppTheme.styleLegacyWindow(getContentPane());
        loadPatients();

        setVisible(true);
    }


    // =====================================================
    // CREATE UI
    // =====================================================

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                new Color(245, 247, 250)
        );


        // ================= HEADER =================

        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(
                new Color(30, 55, 80)
        );

        header.setPreferredSize(
                new Dimension(1250, 70)
        );

        JLabel title =
                new JLabel(
                        "  👤 PATIENT MANAGEMENT"
                );

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        header.add(
                title,
                BorderLayout.WEST
        );


        // ================= FORM =================

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Patient Information"
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        nameField =
                new JTextField(15);

        genderBox =
                new JComboBox<>(
                        new String[]{
                                "Male",
                                "Female",
                                "Other"
                        }
                );

        dobField =
                new JTextField(15);

        phoneField =
                new JTextField(15);

        emailField =
                new JTextField(15);

        addressField =
                new JTextField(15);

        bloodGroupBox =
                new JComboBox<>(
                        new String[]{
                                "A+",
                                "A-",
                                "B+",
                                "B-",
                                "AB+",
                                "AB-",
                                "O+",
                                "O-"
                        }
                );

        emergencyField =
                new JTextField(15);


        addFormField(
                formPanel,
                gbc,
                0,
                0,
                "Name:",
                nameField
        );

        addFormField(
                formPanel,
                gbc,
                2,
                0,
                "Gender:",
                genderBox
        );

        addFormField(
                formPanel,
                gbc,
                0,
                1,
                "DOB (YYYY-MM-DD):",
                dobField
        );

        addFormField(
                formPanel,
                gbc,
                2,
                1,
                "Phone:",
                phoneField
        );

        addFormField(
                formPanel,
                gbc,
                0,
                2,
                "Email:",
                emailField
        );

        addFormField(
                formPanel,
                gbc,
                2,
                2,
                "Blood Group:",
                bloodGroupBox
        );

        addFormField(
                formPanel,
                gbc,
                0,
                3,
                "Address:",
                addressField
        );

        addFormField(
                formPanel,
                gbc,
                2,
                3,
                "Emergency Contact:",
                emergencyField
        );


        // ================= BUTTONS =================

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.setBackground(Color.WHITE);

        JButton addButton =
                new JButton("➕ Add Patient");

        JButton updateButton =
                new JButton("✏️ Update");

        JButton deleteButton =
                new JButton("🗑️ Delete");

        JButton clearButton =
                new JButton("🔄 Clear");

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);


        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;

        formPanel.add(
                buttonPanel,
                gbc
        );


        // ================= TABLE =================

        String[] columns = {
                "ID",
                "Name",
                "Gender",
                "DOB",
                "Phone",
                "Email",
                "Address",
                "Blood Group",
                "Emergency"
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


        patientTable =
                new JTable(tableModel);

        patientTable.setRowHeight(28);

        patientTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        patientTable
                );

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Registered Patients"
                )
        );


        // ================= EVENTS =================

        addButton.addActionListener(
                e -> addPatient()
        );

        updateButton.addActionListener(
                e -> updatePatient()
        );

        deleteButton.addActionListener(
                e -> deletePatient()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );


        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean selected = patientTable.getSelectedRow() >= 0;
            updateButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
            selectPatient();
        });


        // ================= LAYOUT =================

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(top, BorderLayout.NORTH);
        searchableTable = ui.components.SearchableTablePanel.wrap(patientTable, scrollPane,
                "Search by name, phone, or email", this::loadPatients, 1, 4, 5);
        mainPanel.add(searchableTable, BorderLayout.CENTER);

        setMinimumSize(new Dimension(900, 650));
        scrollPane.setPreferredSize(new Dimension(0, 360));
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {55, 170, 90, 110, 130, 210, 260, 100, 160};
        for (int column = 0; column < widths.length; column++) {
            patientTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }

        add(mainPanel);
    }


    // =====================================================
    // FORM FIELD HELPER
    // =====================================================

    private void addFormField(
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

        if (component instanceof JTextField field) {
            field.setColumns(label.startsWith("Address") ? 32 : 22);
        } else if (component instanceof JComboBox<?>) {
            component.setPreferredSize(new Dimension(220, 36));
        }
        panel.add(component, gbc);
    }


    // =====================================================
    // ADD PATIENT
    // =====================================================

    private void addPatient() {

        if (!validateFields()) {
            return;
        }

        String name = nameField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();
        String dob = dobField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String bloodGroup = bloodGroupBox.getSelectedItem().toString();
        String emergency = emergencyField.getText().trim();
        runPatientMutation("Saving patient…", "Patient added successfully.", "Could not add patient.",
                () -> patientService.addPatient(name, gender, dob, phone, email, address, bloodGroup, emergency));
    }


    // =====================================================
    // UPDATE PATIENT
    // =====================================================

    private void updatePatient() {

        if (selectedPatientId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a patient first."
            );

            return;
        }

        if (!validateFields()) {
            return;
        }

        int patientId = selectedPatientId;
        String name = nameField.getText().trim();
        String gender = genderBox.getSelectedItem().toString();
        String dob = dobField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String bloodGroup = bloodGroupBox.getSelectedItem().toString();
        String emergency = emergencyField.getText().trim();
        runPatientMutation("Updating patient…", "Patient updated successfully.", "Could not update patient.",
                () -> patientService.updatePatient(patientId, name, gender, dob, phone, email,
                        address, bloodGroup, emergency));
    }


    // =====================================================
    // DELETE PATIENT
    // =====================================================

    private void deletePatient() {

        if (selectedPatientId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a patient first."
            );

            return;
        }

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this patient?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        int patientId = selectedPatientId;
        runPatientMutation("Deleting patient…", "Patient deleted successfully.",
                "Unable to delete patient. It may have related records.",
                () -> patientService.deletePatient(patientId));
    }

    private void runPatientMutation(String progress, String successMessage, String failureMessage,
                                    java.util.concurrent.Callable<Boolean> operation) {
        searchableTable.setLoading(progress);
        ui.components.SwingTaskRunner.run(getContentPane(), operation, success -> {
            if (success) {
                JOptionPane.showMessageDialog(this, successMessage, "Patient Management",
                        JOptionPane.INFORMATION_MESSAGE);
                searchableTable.setLoaded();
                clearFields();
                loadPatients();
            } else {
                searchableTable.setError(failureMessage);
                JOptionPane.showMessageDialog(this, failureMessage, "Patient Management",
                        JOptionPane.ERROR_MESSAGE);
            }
        }, exception -> {
            String message = "Unable to complete the patient operation. Check the database connection and retry.";
            searchableTable.setError(message);
            JOptionPane.showMessageDialog(this, message, "Patient Management", JOptionPane.ERROR_MESSAGE);
        });
    }


    // =====================================================
    // LOAD PATIENTS
    // =====================================================

    private void loadPatients() {
        searchableTable.loadAsync("Loading patient records…",
                "Could not load patients. Check the database connection and retry.",
                patientService::getAllPatients, patients -> {
                    tableModel.setRowCount(0);
                    for (Object[] patient : patients) tableModel.addRow(patient);
                });
    }


    // =====================================================
    // SELECT PATIENT
    // =====================================================

    private void selectPatient() {

        int row =
                patientTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedPatientId =
                Integer.parseInt(
                        patientTable
                                .getValueAt(row, 0)
                                .toString()
                );

        nameField.setText(
                value(row, 1)
        );

        genderBox.setSelectedItem(
                value(row, 2)
        );

        dobField.setText(
                value(row, 3)
        );

        phoneField.setText(
                value(row, 4)
        );

        emailField.setText(
                value(row, 5)
        );

        addressField.setText(
                value(row, 6)
        );

        bloodGroupBox.setSelectedItem(
                value(row, 7)
        );

        emergencyField.setText(
                value(row, 8)
        );
    }


    private String value(
            int row,
            int column) {

        Object value =
                patientTable.getValueAt(
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

        if (nameField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Patient name is required."
            );

            nameField.requestFocus();

            return false;
        }

        if (!dobField.getText().trim().isEmpty()) {

            try {

                java.sql.Date.valueOf(
                        dobField.getText().trim()
                );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "DOB must be in YYYY-MM-DD format."
                );

                return false;
            }
        }

        return true;
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        selectedPatientId = -1;

        nameField.setText("");
        dobField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        emergencyField.setText("");

        genderBox.setSelectedIndex(0);
        bloodGroupBox.setSelectedIndex(0);

        patientTable.clearSelection();
    }
}
