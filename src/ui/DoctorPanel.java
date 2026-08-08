package ui;

import dao.DoctorDAO;

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

    private DoctorDAO doctorDAO;

    private int selectedDoctorId = -1;


    public DoctorPanel() {

        doctorDAO = new DoctorDAO();

        setTitle("Hospital Management System - Doctor Management");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        createUI();
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


        doctorTable.getSelectionModel()
                .addListSelectionListener(
                        e -> selectDoctor()
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

        scrollPane.setPreferredSize(
                new Dimension(1200, 330)
        );

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
    // ADD DOCTOR
    // =====================================================

    private void addDoctor() {

        if (!validateFields()) {
            return;
        }

        boolean success =
                doctorDAO.addDoctor(
                        nameField.getText().trim(),
                        specializationField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        experienceField.getText().trim(),
                        feeField.getText().trim(),
                        availabilityBox
                                .getSelectedItem()
                                .toString()
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Doctor added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            loadDoctors();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to add doctor.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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


        boolean success =
                doctorDAO.updateDoctor(
                        selectedDoctorId,
                        nameField.getText().trim(),
                        specializationField.getText().trim(),
                        phoneField.getText().trim(),
                        emailField.getText().trim(),
                        experienceField.getText().trim(),
                        feeField.getText().trim(),
                        availabilityBox
                                .getSelectedItem()
                                .toString()
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Doctor updated successfully!"
            );

            clearFields();
            loadDoctors();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Failed to update doctor.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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


        boolean success =
                doctorDAO.deleteDoctor(
                        selectedDoctorId
                );


        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Doctor deleted successfully!"
            );

            clearFields();
            loadDoctors();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete doctor.\n" +
                    "The doctor may have related appointments.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =====================================================
    // LOAD DOCTORS
    // =====================================================

    private void loadDoctors() {

        tableModel.setRowCount(0);

        List<Object[]> doctors =
                doctorDAO.getAllDoctors();

        for (Object[] doctor : doctors) {

            tableModel.addRow(doctor);
        }
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