package ui;

import dao.BillDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BillingPanel extends JFrame {

    private JTextField billIdField;
    private JTextField patientIdField;
    private JTextField admissionIdField;

    private JTextField roomChargesField;
    private JTextField doctorChargesField;
    private JTextField medicineChargesField;
    private JTextField otherChargesField;

    private JTextField totalAmountField;
    private JTextField paidAmountField;

    private JComboBox<String> paymentStatusCombo;

    private JTable billTable;
    private DefaultTableModel tableModel;

    private final BillDAO billDAO = new BillDAO();

    public BillingPanel() {

        setTitle("Hospital Management System - Billing");
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        loadBills();
    }

    // =========================================================
    // BUILD UI
    // =========================================================

    private void buildUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 65, 94));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("Billing & Payments");

        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));

        titleLabel.setBorder(
                new EmptyBorder(0, 30, 0, 0)
        );

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // -----------------------------------------------------
        // FORM PANEL
        // -----------------------------------------------------

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBackground(Color.WHITE);

        formPanel.setBorder(
                new EmptyBorder(20, 30, 20, 30)
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        billIdField = createField();
        patientIdField = createField();
        admissionIdField = createField();

        roomChargesField = createField();
        doctorChargesField = createField();
        medicineChargesField = createField();
        otherChargesField = createField();

        totalAmountField = createField();
        paidAmountField = createField();

        totalAmountField.setEditable(false);

        paymentStatusCombo =
                new JComboBox<>(
                        new String[]{
                                "PENDING",
                                "PARTIAL",
                                "PAID"
                        }
                );

        // Row 0

        addField(
                formPanel,
                gbc,
                0,
                0,
                "Bill ID:",
                billIdField
        );

        addField(
                formPanel,
                gbc,
                2,
                0,
                "Patient ID:",
                patientIdField
        );

        addField(
                formPanel,
                gbc,
                4,
                0,
                "Admission ID:",
                admissionIdField
        );

        // Row 1

        addField(
                formPanel,
                gbc,
                0,
                1,
                "Room Charges:",
                roomChargesField
        );

        addField(
                formPanel,
                gbc,
                2,
                1,
                "Doctor Charges:",
                doctorChargesField
        );

        addField(
                formPanel,
                gbc,
                4,
                1,
                "Medicine Charges:",
                medicineChargesField
        );

        // Row 2

        addField(
                formPanel,
                gbc,
                0,
                2,
                "Other Charges:",
                otherChargesField
        );

        addField(
                formPanel,
                gbc,
                2,
                2,
                "Total Amount:",
                totalAmountField
        );

        addField(
                formPanel,
                gbc,
                4,
                2,
                "Paid Amount:",
                paidAmountField
        );

        // Row 3

        addField(
                formPanel,
                gbc,
                0,
                3,
                "Payment Status:",
                paymentStatusCombo
        );

        // -----------------------------------------------------
        // BUTTON PANEL
        // -----------------------------------------------------

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                15,
                                10
                        )
                );

        buttonPanel.setBackground(Color.WHITE);

        JButton addButton =
                createButton("Generate Bill");

        JButton updateButton =
                createButton("Update");

        JButton paymentButton =
                createButton("Add Payment");

        JButton deleteButton =
                createButton("Delete");

        JButton clearButton =
                createButton("Clear");

        JButton refreshButton =
                createButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(paymentButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        addButton.addActionListener(
                e -> addBill()
        );

        updateButton.addActionListener(
                e -> updateBill()
        );

        paymentButton.addActionListener(
                e -> addPayment()
        );

        deleteButton.addActionListener(
                e -> deleteBill()
        );

        clearButton.addActionListener(
                e -> clearFields()
        );

        refreshButton.addActionListener(
                e -> loadBills()
        );

        // -----------------------------------------------------
        // TOP SECTION
        // -----------------------------------------------------

        JPanel topPanel =
                new JPanel(new BorderLayout());

        topPanel.setBackground(Color.WHITE);

        topPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        topPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // -----------------------------------------------------
        // TABLE
        // -----------------------------------------------------

        String[] columns = {

                "Bill ID",
                "Patient ID",
                "Admission ID",
                "Room",
                "Doctor",
                "Medicine",
                "Other",
                "Total",
                "Paid",
                "Status",
                "Date"
        };

        tableModel =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        billTable =
                new JTable(tableModel);

        billTable.setRowHeight(30);

        billTable.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        billTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                14
                        )
                );

        billTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane =
                new JScrollPane(billTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Bill Records"
                )
        );

        // -----------------------------------------------------
        // TABLE CLICK
        // -----------------------------------------------------

        billTable.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        int row =
                                billTable.getSelectedRow();

                        if (row >= 0) {
                            loadSelectedBill(row);
                        }
                    }
                }
        );

        JPanel centerPanel =
                new JPanel(new BorderLayout());

        centerPanel.setBorder(
                new EmptyBorder(
                        10,
                        15,
                        15,
                        15
                )
        );

        centerPanel.setBackground(
                new Color(245, 247, 250)
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                topPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        setContentPane(mainPanel);
    }

    // =========================================================
    // CREATE FIELD
    // =========================================================

    private JTextField createField() {

        JTextField field =
                new JTextField();

        field.setPreferredSize(
                new Dimension(150, 32)
        );

        field.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        14
                )
        );

        return field;
    }

    // =========================================================
    // ADD FIELD
    // =========================================================

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int x,
            int y,
            String labelText,
            Component component
    ) {

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;

        JLabel label =
                new JLabel(labelText);

        label.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        panel.add(label, gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1;

        panel.add(component, gbc);
    }

    // =========================================================
    // CREATE BUTTON
    // =========================================================

    private JButton createButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        13
                )
        );

        button.setPreferredSize(
                new Dimension(125, 35)
        );

        return button;
    }

    // =========================================================
    // CALCULATE TOTAL
    // =========================================================

    private double calculateTotal() {

        double room =
                parseAmount(
                        roomChargesField.getText()
                );

        double doctor =
                parseAmount(
                        doctorChargesField.getText()
                );

        double medicine =
                parseAmount(
                        medicineChargesField.getText()
                );

        double other =
                parseAmount(
                        otherChargesField.getText()
                );

        double total =
                room
                + doctor
                + medicine
                + other;

        totalAmountField.setText(
                String.format(
                        "%.2f",
                        total
                )
        );

        return total;
    }

    // =========================================================
    // PARSE AMOUNT
    // =========================================================

    private double parseAmount(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return 0;
        }

        return Double.parseDouble(
                value.trim()
        );
    }

    // =========================================================
    // GET INTEGER
    // =========================================================

    private int getInteger(
            JTextField field,
            String fieldName
    ) throws Exception {

        if (field.getText()
                .trim()
                .isEmpty()) {

            throw new Exception(
                    fieldName +
                    " is required."
            );
        }

        return Integer.parseInt(
                field.getText().trim()
        );
    }

    // =========================================================
    // ADD BILL
    // =========================================================

    private void addBill() {

        try {

            int patientId =
                    getInteger(
                            patientIdField,
                            "Patient ID"
                    );

            int admissionId =
                    getInteger(
                            admissionIdField,
                            "Admission ID"
                    );

            double room =
                    parseAmount(
                            roomChargesField.getText()
                    );

            double doctor =
                    parseAmount(
                            doctorChargesField.getText()
                    );

            double medicine =
                    parseAmount(
                            medicineChargesField.getText()
                    );

            double other =
                    parseAmount(
                            otherChargesField.getText()
                    );

            double paid =
                    parseAmount(
                            paidAmountField.getText()
                    );

            double total =
                    calculateTotal();

            if (total <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Total amount must be greater than 0.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (paid < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (paid > total) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot exceed total amount.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            boolean success =
                    billDAO.addBill(
                            patientId,
                            admissionId,
                            room,
                            doctor,
                            medicine,
                            other,
                            paid
                    );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Bill generated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadBills();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Failed to generate bill.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numeric values.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // UPDATE BILL
    // =========================================================

    private void updateBill() {

        try {

            int billId =
                    getInteger(
                            billIdField,
                            "Bill ID"
                    );

            int patientId =
                    getInteger(
                            patientIdField,
                            "Patient ID"
                    );

            int admissionId =
                    getInteger(
                            admissionIdField,
                            "Admission ID"
                    );

            double room =
                    parseAmount(
                            roomChargesField.getText()
                    );

            double doctor =
                    parseAmount(
                            doctorChargesField.getText()
                    );

            double medicine =
                    parseAmount(
                            medicineChargesField.getText()
                    );

            double other =
                    parseAmount(
                            otherChargesField.getText()
                    );

            double paid =
                    parseAmount(
                            paidAmountField.getText()
                    );

            double total =
                    calculateTotal();

            if (paid < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (paid > total) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot exceed total amount.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            boolean success =
                    billDAO.updateBill(
                            billId,
                            patientId,
                            admissionId,
                            room,
                            doctor,
                            medicine,
                            other,
                            paid
                    );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Bill updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadBills();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Bill update failed.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numeric values.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD PAYMENT
    // =========================================================

    private void addPayment() {

        try {

            int billId =
                    getInteger(
                            billIdField,
                            "Bill ID"
                    );

            // Get latest bill information
            Object[] bill =
                    billDAO.getBillById(billId);

            if (bill == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Bill ID " + billId + " not found.",
                        "Bill Not Found",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            double totalAmount =
                    ((Number) bill[7]).doubleValue();

            double paidAmount =
                    ((Number) bill[8]).doubleValue();

            double remainingAmount =
                    totalAmount - paidAmount;

            // Already paid
            if (remainingAmount <= 0.001) {

                JOptionPane.showMessageDialog(
                        this,
                        "This bill is already fully paid.",
                        "Payment Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            // Payment input
            String input =
                    JOptionPane.showInputDialog(
                            this,
                            String.format(
                                    "Total Amount: ₹%.2f%n"
                                    + "Already Paid: ₹%.2f%n"
                                    + "Remaining: ₹%.2f%n%n"
                                    + "Enter payment amount:",
                                    totalAmount,
                                    paidAmount,
                                    remainingAmount
                            ),
                            "Make Payment",
                            JOptionPane.PLAIN_MESSAGE
                    );

            if (input == null) {
                return;
            }

            input = input.trim();

            if (input.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Payment amount is required.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            double paymentAmount =
                    Double.parseDouble(input);

            // Positive payment
            if (paymentAmount <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Payment must be greater than ₹0.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Prevent overpayment
            if (paymentAmount > remainingAmount) {

                JOptionPane.showMessageDialog(
                        this,
                        String.format(
                                "Payment cannot exceed "
                                + "the remaining amount.%n%n"
                                + "Remaining Amount: ₹%.2f",
                                remainingAmount
                        ),
                        "Invalid Payment",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // Confirm payment
            int confirmation =
                    JOptionPane.showConfirmDialog(
                            this,
                            String.format(
                                    "Confirm payment of ₹%.2f?",
                                    paymentAmount
                            ),
                            "Confirm Payment",
                            JOptionPane.YES_NO_OPTION
                    );

            if (confirmation !=
                    JOptionPane.YES_OPTION) {

                return;
            }

            // Save payment
            boolean success =
                    billDAO.addPayment(
                            billId,
                            paymentAmount
                    );

            if (success) {

                double newPaidAmount =
                        paidAmount + paymentAmount;

                double newRemainingAmount =
                        totalAmount - newPaidAmount;

                String status;

                if (newRemainingAmount <= 0.001) {

                    status = "PAID";
                    newRemainingAmount = 0;

                } else {

                    status = "PARTIAL";
                }

                // Update form
                paidAmountField.setText(
                        String.format(
                                "%.2f",
                                newPaidAmount
                        )
                );

                paymentStatusCombo.setSelectedItem(
                        status
                );

                JOptionPane.showMessageDialog(
                        this,
                        String.format(
                                "Payment successful!%n%n"
                                + "Bill ID: %d%n"
                                + "Payment: ₹%.2f%n"
                                + "Total Paid: ₹%.2f%n"
                                + "Remaining: ₹%.2f%n"
                                + "Status: %s",
                                billId,
                                paymentAmount,
                                newPaidAmount,
                                newRemainingAmount,
                                status
                        ),
                        "Payment Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadBills();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Payment failed. Please check "
                                + "the Bill ID and payment amount.",
                        "Payment Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid numeric "
                            + "payment amount.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error processing payment:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // DELETE BILL
    // =========================================================

    private void deleteBill() {

        try {

            int billId =
                    getInteger(
                            billIdField,
                            "Bill ID"
                    );

            int confirmation =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to delete Bill ID "
                                    + billId
                                    + "?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                    );

            if (confirmation !=
                    JOptionPane.YES_OPTION) {

                return;
            }

            boolean success =
                    billDAO.deleteBill(billId);

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Bill deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadBills();
                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Bill not found or delete failed.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // LOAD BILLS
    // =========================================================

    private void loadBills() {

        tableModel.setRowCount(0);

        List<Object[]> bills =
                billDAO.getAllBills();

        for (Object[] bill : bills) {

            tableModel.addRow(bill);
        }
    }

    // =========================================================
    // LOAD SELECTED BILL
    // =========================================================

    private void loadSelectedBill(
            int row
    ) {

        billIdField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 0)
                )
        );

        patientIdField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 1)
                )
        );

        admissionIdField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 2)
                )
        );

        roomChargesField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 3)
                )
        );

        doctorChargesField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 4)
                )
        );

        medicineChargesField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 5)
                )
        );

        otherChargesField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 6)
                )
        );

        totalAmountField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 7)
                )
        );

        paidAmountField.setText(
                String.valueOf(
                        tableModel.getValueAt(row, 8)
                )
        );

        paymentStatusCombo.setSelectedItem(
                String.valueOf(
                        tableModel.getValueAt(row, 9)
                )
        );
    }

    // =========================================================
    // CLEAR
    // =========================================================

    private void clearFields() {

        billIdField.setText("");
        patientIdField.setText("");
        admissionIdField.setText("");

        roomChargesField.setText("");
        doctorChargesField.setText("");
        medicineChargesField.setText("");
        otherChargesField.setText("");

        totalAmountField.setText("");
        paidAmountField.setText("");

        paymentStatusCombo.setSelectedItem(
                "PENDING"
        );

        billTable.clearSelection();
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> {

                    BillingPanel panel =
                            new BillingPanel();

                    panel.setVisible(true);
                }
        );
    }
}