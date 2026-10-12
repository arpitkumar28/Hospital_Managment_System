package ui;

import service.DoctorService;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JFrame {

    private JTextField nameField;
    private JTextField specializationField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField experienceField;
    private JTextField feeField;

    private JComboBox<String> availabilityBox;

    private JTable doctorTable;
    private DefaultTableModel tableModel;

    private DoctorService doctorService;

    private int selectedDoctorId = -1;
    private ui.components.SearchableTablePanel searchableTable;


    public DoctorPanel() {

        doctorService = new DoctorService();

        setTitle("Hospital Management System - Doctor Management");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
        AppTheme.styleLegacyWindow(getContentPane());
        loadDoctors();

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
                new Dimension(1200, 70)
        );

        JLabel title =
                new JLabel(
                        "  👨‍⚕️ DOCTOR MANAGEMENT"
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
                        "Doctor Information"
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

        specializationField =
                new JTextField(15);

        phoneField =
                new JTextField(15);

        emailField =
                new JTextField(15);

        experienceField =
                new JTextField(15);

        feeField =
                new JTextField(15);

        availabilityBox =
                new JComboBox<>(
                        new String[]{
                                "Available",
                                "Not Available",
                                "On Leave"
                        }
                );


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
                "Specialization:",
                specializationField
        );

        addFormField(
                formPanel,
                gbc,
                0,
                1,
                "Phone:",
                phoneField
        );

        addFormField(
                formPanel,
                gbc,
                2,
                1,
                "Email:",
                emailField
        );

        addFormField(
                formPanel,
                gbc,
                0,
                2,
                "Experience (Years):",
                experienceField
        );

        addFormField(
                formPanel,
                gbc,
                2,
                2,
                "Consultation Fee:",
                feeField
        );

        addFormField(
                formPanel,
                gbc,
                0,
                3,
                "Availability:",
                availabilityBox
        );


        // ================= BUTTONS =================

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.setBackground(Color.WHITE);

        JButton addButton =
                new JButton("➕ Add Doctor");

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
                "Specialization",
                "Phone",
                "Email",
                "Experience",
                "Fee",
                "Availability"
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


        doctorTable =
                new JTable(tableModel);

        doctorTable.setRowHeight(28);

        doctorTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        doctorTable
                );

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Registered Doctors"
                )
        );


        // ================= EVENTS =================

        addButton.addActionListener(
                e -> addDoctor()
        );

        updateButton.addActionListener(
                e -> updateDoctor()
        );

        deleteButton.addActionListener(
                e -> deleteDoctor()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );


        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            boolean selected = doctorTable.getSelectedRow() >= 0;
            updateButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
            selectDoctor();
        });


        // ================= LAYOUT =================

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(top, BorderLayout.NORTH);
        searchableTable = ui.components.SearchableTablePanel.wrap(doctorTable, scrollPane,
                "Search by name, specialty, or email", this::loadDoctors, 1, 2, 4);
        mainPanel.add(searchableTable, BorderLayout.CENTER);

        setMinimumSize(new Dimension(900, 620));
        scrollPane.setPreferredSize(new Dimension(0, 340));
        doctorTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        int[] widths = {55, 180, 190, 135, 220, 115, 145, 150};
        for (int column = 0; column < widths.length; column++) {
            doctorTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }

        add(mainPanel);
    }


    // =====================================================
    // FORM FIELD
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

        if (component instanceof JTextField field) field.setColumns(22);
        panel.add(component, gbc);
    }


    // =====================================================
    // ADD DOCTOR
    // =====================================================

    private void addDoctor() {

        if (!validateFields()) {
            return;
        }

        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String experience = experienceField.getText().trim();
        String fee = feeField.getText().trim();
        String availability = availabilityBox.getSelectedItem().toString();
        runDoctorMutation("Saving doctor…", "Doctor added successfully.", "Could not add doctor.",
                () -> doctorService.addDoctor(name, specialization, phone, email, experience, fee, availability));
    }


    // =====================================================
    // UPDATE DOCTOR
    // =====================================================

    private void updateDoctor() {

        if (selectedDoctorId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a doctor first."
            );

            return;
        }


        if (!validateFields()) {
            return;
        }


        int doctorId = selectedDoctorId;
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String experience = experienceField.getText().trim();
        String fee = feeField.getText().trim();
        String availability = availabilityBox.getSelectedItem().toString();
        runDoctorMutation("Updating doctor…", "Doctor updated successfully.", "Could not update doctor.",
                () -> doctorService.updateDoctor(doctorId, name, specialization, phone, email,
                        experience, fee, availability));
    }


    // =====================================================
    // DELETE DOCTOR
    // =====================================================

    private void deleteDoctor() {

        if (selectedDoctorId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a doctor first."
            );

            return;
        }


        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this doctor?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );


        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }


        int doctorId = selectedDoctorId;
        runDoctorMutation("Deleting doctor…", "Doctor deleted successfully.",
                "Unable to delete doctor. It may have related appointments.",
                () -> doctorService.deleteDoctor(doctorId));
    }

    private void runDoctorMutation(String progress, String successMessage, String failureMessage,
                                   java.util.concurrent.Callable<Boolean> operation) {
        searchableTable.setLoading(progress);
        ui.components.SwingTaskRunner.run(getContentPane(), operation, success -> {
            if (success) {
                JOptionPane.showMessageDialog(this, successMessage, "Doctor Management",
                        JOptionPane.INFORMATION_MESSAGE);
                searchableTable.setLoaded();
                clearFields();
                loadDoctors();
            } else {
                searchableTable.setError(failureMessage);
                JOptionPane.showMessageDialog(this, failureMessage, "Doctor Management",
                        JOptionPane.ERROR_MESSAGE);
            }
        }, exception -> {
            String message = "Unable to complete the doctor operation. Check the database connection and retry.";
            searchableTable.setError(message);
            JOptionPane.showMessageDialog(this, message, "Doctor Management", JOptionPane.ERROR_MESSAGE);
        });
    }


    // =====================================================
    // LOAD DOCTORS
    // =====================================================

    private void loadDoctors() {
        searchableTable.loadAsync("Loading doctor records…",
                "Could not load doctors. Check the database connection and retry.",
                doctorService::getAllDoctors, doctors -> {
                    tableModel.setRowCount(0);
                    for (Object[] doctor : doctors) tableModel.addRow(doctor);
                });
    }


    // =====================================================
    // SELECT DOCTOR
    // =====================================================

    private void selectDoctor() {

        int row =
                doctorTable.getSelectedRow();

        if (row == -1) {
            return;
        }


        selectedDoctorId =
                Integer.parseInt(
                        doctorTable
                                .getValueAt(row, 0)
                                .toString()
                );


        nameField.setText(
                value(row, 1)
        );

        specializationField.setText(
                value(row, 2)
        );

        phoneField.setText(
                value(row, 3)
        );

        emailField.setText(
                value(row, 4)
        );

        experienceField.setText(
                value(row, 5)
        );

        feeField.setText(
                value(row, 6)
        );

        availabilityBox.setSelectedItem(
                value(row, 7)
        );
    }


    private String value(
            int row,
            int column) {

        Object value =
                doctorTable.getValueAt(
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

        if (nameField.getText()
                .trim()
                .isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Doctor name is required."
            );

            nameField.requestFocus();

            return false;
        }


        if (specializationField.getText()
                .trim()
                .isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Specialization is required."
            );

            specializationField.requestFocus();

            return false;
        }


        try {

            int experience =
                    Integer.parseInt(
                            experienceField
                                    .getText()
                                    .trim()
                    );

            if (experience < 0) {
                throw new NumberFormatException();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Experience must be a valid number."
            );

            experienceField.requestFocus();

            return false;
        }


        try {

            double fee =
                    Double.parseDouble(
                            feeField
                                    .getText()
                                    .trim()
                    );

            if (fee < 0) {
                throw new NumberFormatException();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Consultation fee must be a valid number."
            );

            feeField.requestFocus();

            return false;
        }


        return true;
    }


    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        selectedDoctorId = -1;

        nameField.setText("");
        specializationField.setText("");
        phoneField.setText("");
        emailField.setText("");
        experienceField.setText("");
        feeField.setText("");

        availabilityBox.setSelectedIndex(0);

        doctorTable.clearSelection();
    }
}
