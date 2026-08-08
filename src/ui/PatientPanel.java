package ui;

import dao.PatientDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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

    private PatientDAO patientDAO;

    private int selectedPatientId = -1;


    public PatientPanel() {

        patientDAO = new PatientDAO();

        setTitle("Hospital Management System - Patient Management");
        setSize(1250, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
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


        patientTable.getSelectionModel()
                .addListSelectionListener(
                        e -> selectPatient()
                );


        // ================= LAYOUT =================

        mainPanel.add(
                header,
                BorderLayout.NORTH
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                scrollPane,
                BorderLayout.SOUTH
        );

        // Give table more space
        scrollPane.setPreferredSize(
                new Dimension(1250, 350)
        );

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
    // ADD PATIENT
    // =====================================================

    private void addPatient() {

        if (!validateFields()) {
            return;
        }

        boolean success =
                patientDAO.addPatient(
                        nameField.getText().trim(),
                        genderBox.getSelectedItem().toString(),
                        dobField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        addressField.getText().trim(),
                        bloodGroupBox.getSelectedItem().toString(),
                        emergencyField.getText().trim()
                );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Patient added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            loadPatients();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to add patient.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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

        boolean success =
                patientDAO.updatePatient(
                        selectedPatientId,
                        nameField.getText().trim(),
                        genderBox.getSelectedItem().toString(),
                        dobField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        addressField.getText().trim(),
                        bloodGroupBox.getSelectedItem().toString(),
                        emergencyField.getText().trim()
                );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Patient updated successfully!"
            );

            clearFields();
            loadPatients();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to update patient.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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

        boolean success =
                patientDAO.deletePatient(
                        selectedPatientId
                );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Patient deleted successfully!"
            );

            clearFields();
            loadPatients();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete patient.\n" +
                    "The patient may have related records.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // LOAD PATIENTS
    // =====================================================

    private void loadPatients() {

        tableModel.setRowCount(0);

        List<Object[]> patients =
                patientDAO.getAllPatients();

        for (Object[] patient : patients) {

            tableModel.addRow(patient);
        }
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