package ui;

import service.BillingService;
import ui.theme.AppTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class BillingPanel extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillingPanel.class);

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
    private TableRowSorter<DefaultTableModel> billSorter;
    private JButton addButton;
    private JButton updateButton;
    private JButton paymentButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private boolean databaseBusy;

    private final BillingService billingService = new BillingService();

    public BillingPanel() {

        setTitle("Hospital Management System - Billing");
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        AppTheme.styleLegacyWindow(getContentPane());
        loadBills();
        setVisible(true);
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

        addButton =
                createButton("Generate Bill");

        updateButton =
                createButton("Update");

        paymentButton =
                createButton("Add Payment");

        deleteButton =
                createButton("Delete");

        clearButton =
                createButton("Clear");

        refreshButton =
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
        billSorter = new TableRowSorter<>(tableModel);
        billTable.setRowSorter(billSorter);

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

                        int viewRow =
                                billTable.getSelectedRow();

                        if (viewRow >= 0) {
                            loadSelectedBill(billTable.convertRowIndexToModel(viewRow));
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

        JTextField searchField = new JTextField(24);
        searchField.putClientProperty("JTextField.placeholderText", "Search bills by ID, patient, or status");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateFilter() {
                String text = searchField.getText().trim();
                billSorter.setRowFilter(text.isEmpty() ? null
                        : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }

            @Override public void insertUpdate(javax.swing.event.DocumentEvent event) { updateFilter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent event) { updateFilter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent event) { updateFilter(); }
        });
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 8, 0));
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );
        centerPanel.add(searchPanel, BorderLayout.NORTH);

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

    private BigDecimal calculateTotal() {

        BigDecimal room =
                parseAmount(
                        roomChargesField.getText()
                );

        BigDecimal doctor =
                parseAmount(
                        doctorChargesField.getText()
                );

        BigDecimal medicine =
                parseAmount(
                        medicineChargesField.getText()
                );

        BigDecimal other =
                parseAmount(
                        otherChargesField.getText()
                );

        BigDecimal total = room.add(doctor).add(medicine).add(other);

        totalAmountField.setText(total.setScale(2, RoundingMode.UNNECESSARY).toPlainString());

        return total;
    }

    // =========================================================
    // PARSE AMOUNT
    // =========================================================

    private BigDecimal parseAmount(
            String value
    ) {

        if (value == null ||
                value.trim().isEmpty()) {

            return BigDecimal.ZERO.setScale(2);
        }

        return new BigDecimal(value.trim()).setScale(2, RoundingMode.UNNECESSARY);
    }

    // =========================================================
    // GET INTEGER
    // =========================================================

    private int getInteger(
            JTextField field,
            String fieldName
    ) {

        if (field.getText()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(fieldName + " is required.");
        }

        int value = Integer.parseInt(field.getText().trim());
        if (value <= 0) throw new IllegalArgumentException(fieldName + " must be a positive number.");
        return value;
    }

    private Integer getOptionalInteger(JTextField field, String fieldName) {
        if (field.getText().isBlank()) return null;
        return getInteger(field, fieldName);
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

            Integer admissionId =
                    getOptionalInteger(
                            admissionIdField,
                            "Admission ID"
                    );

            BigDecimal room =
                    parseAmount(
                            roomChargesField.getText()
                    );

            BigDecimal doctor =
                    parseAmount(
                            doctorChargesField.getText()
                    );

            BigDecimal medicine =
                    parseAmount(
                            medicineChargesField.getText()
                    );

            BigDecimal other =
                    parseAmount(
                            otherChargesField.getText()
                    );

            BigDecimal paid =
                    parseAmount(
                            paidAmountField.getText()
                    );

            BigDecimal total =
                    calculateTotal();

            if (total.signum() <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Total amount must be greater than 0.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (paid.signum() < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (paid.compareTo(total) > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot exceed total amount.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            runDatabaseTask("save bill",
                    () -> billingService.addBill(patientId, admissionId, room, doctor, medicine, other, paid),
                    success -> {
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Bill generated successfully.",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearFields();
                            loadBills();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Patient or admission was not found, or the admission belongs to a different patient.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numeric values.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (ArithmeticException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Currency amounts may have no more than two decimal places.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            LOGGER.error("Unable to save bill.", e);

            JOptionPane.showMessageDialog(
                    this,
                    databaseErrorMessage(e, "save the bill"),
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

            Integer admissionId =
                    getOptionalInteger(
                            admissionIdField,
                            "Admission ID"
                    );

            BigDecimal room =
                    parseAmount(
                            roomChargesField.getText()
                    );

            BigDecimal doctor =
                    parseAmount(
                            doctorChargesField.getText()
                    );

            BigDecimal medicine =
                    parseAmount(
                            medicineChargesField.getText()
                    );

            BigDecimal other =
                    parseAmount(
                            otherChargesField.getText()
                    );

            BigDecimal paid =
                    parseAmount(
                            paidAmountField.getText()
                    );

            BigDecimal total =
                    calculateTotal();

            if (paid.signum() < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot be negative.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (paid.compareTo(total) > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Paid amount cannot exceed total amount.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            runDatabaseTask("update bill",
                    () -> billingService.updateBill(billId, patientId, admissionId, room, doctor,
                            medicine, other, paid),
                    success -> {
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Bill updated successfully.",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearFields();
                            loadBills();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Bill, patient, or matching admission was not found. The saved payment amount cannot be reduced.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numeric values.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (ArithmeticException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Currency amounts may have no more than two decimal places.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (Exception e) {

            LOGGER.error("Unable to update bill.", e);

            JOptionPane.showMessageDialog(
                    this,
                    databaseErrorMessage(e, "update the bill"),
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
            int billId = getInteger(billIdField, "Bill ID");
            runDatabaseTask("load bill details", () -> billingService.getBillById(billId),
                    bill -> processPayment(billId, bill));
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
        }
    }

    private void processPayment(int billId, Object[] bill) {
        if (bill == null) {
            JOptionPane.showMessageDialog(this, "Bill ID " + billId + " was not found.",
                    "Bill Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal total = (BigDecimal) bill[7];
        BigDecimal paid = (BigDecimal) bill[8];
        BigDecimal remaining = total.subtract(paid);
        if (remaining.signum() <= 0) {
            JOptionPane.showMessageDialog(this, "This bill is already fully paid.",
                    "Payment Complete", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                String.format("Total Amount: ₹%.2f%nAlready Paid: ₹%.2f%nRemaining: ₹%.2f%n%nEnter payment amount:",
                        total, paid, remaining), "Make Payment", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        try {
            if (input.isBlank()) throw new IllegalArgumentException("Payment amount is required.");
            BigDecimal amount = new BigDecimal(input.trim()).setScale(2, RoundingMode.UNNECESSARY);
            if (amount.signum() <= 0) throw new IllegalArgumentException("Payment must be greater than ₹0.");
            if (amount.compareTo(remaining) > 0) {
                throw new IllegalArgumentException(String.format(
                        "Payment cannot exceed the remaining amount of ₹%.2f.", remaining));
            }
            int confirmation = JOptionPane.showConfirmDialog(this,
                    String.format("Confirm payment of ₹%.2f?", amount),
                    "Confirm Payment", JOptionPane.YES_NO_OPTION);
            if (confirmation != JOptionPane.YES_OPTION) return;

            runDatabaseTask("record payment", () -> billingService.addPayment(billId, amount), success -> {
                if (!success) {
                    JOptionPane.showMessageDialog(this,
                            "The balance changed or the payment no longer fits. Refresh the bill and try again.",
                            "Payment Not Recorded", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                loadBills(() -> {
                    selectBillInTable(billId);
                    showPaymentRecorded(billId, amount, true);
                }, () -> showPaymentRecorded(billId, amount, false));
            });
        } catch (NumberFormatException exception) {
            showValidationError("Please enter a valid numeric payment amount.");
        } catch (ArithmeticException exception) {
            showValidationError("Payment amounts may have no more than two decimal places.");
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
        }
    }

    private void showPaymentRecorded(int billId, BigDecimal amount, boolean refreshed) {
        JOptionPane.showMessageDialog(this, paymentRecordedMessage(billId, amount, refreshed),
                "Payment Recorded", refreshed ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    private void selectBillInTable(int billId) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            if (Integer.valueOf(billId).equals(tableModel.getValueAt(row, 0))) {
                int viewRow = billTable.convertRowIndexToView(row);
                if (viewRow < 0) return;
                billTable.setRowSelectionInterval(viewRow, viewRow);
                loadSelectedBill(row);
                return;
            }
        }
    }

    static String paymentRecordedMessage(int billId, BigDecimal paymentAmount, boolean refreshed) {
        String message = String.format("Payment of ₹%.2f was recorded for Bill ID %d.", paymentAmount, billId);
        if (!refreshed) {
            message += "\nThe bill list could not be refreshed. Use Refresh to load current balances.";
        }
        return message;
    }

    // =========================================================
    // DELETE BILL
    // =========================================================

    private void deleteBill() {
        try {
            int billId = getInteger(billIdField, "Bill ID");
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Bill ID " + billId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirmation != JOptionPane.YES_OPTION) return;

            runDatabaseTask("delete bill", () -> billingService.deleteBill(billId), success -> {
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
                        "The bill was not found or could not be deleted because it has related records.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                }
            });
        } catch (IllegalArgumentException exception) {
            showValidationError(exception.getMessage());
        }
    }

    // =========================================================
    // LOAD BILLS
    // =========================================================

    private void loadBills() {
        loadBills(null, null);
    }

    private void loadBills(Runnable loaded, Runnable failed) {
        runDatabaseTask("load bills", billingService::getAllBills, rows -> {
            tableModel.setRowCount(0);
            for (Object[] bill : rows) tableModel.addRow(bill);
            if (loaded != null) loaded.run();
        }, error -> {
            LOGGER.error("Unable to refresh bill list.", error);
            if (failed != null) {
                failed.run();
            } else {
                JOptionPane.showMessageDialog(this,
                        databaseErrorMessage(error, "load bills"),
                        "Refresh Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private <T> void runDatabaseTask(String operation, Callable<T> task, Consumer<T> completed) {
        runDatabaseTask(operation, task, completed, error -> {
            LOGGER.error("Unable to {}.", operation, error);
            JOptionPane.showMessageDialog(this, databaseErrorMessage(error, operation),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private <T> void runDatabaseTask(String operation, Callable<T> task, Consumer<T> completed,
                                     Consumer<Throwable> failed) {
        if (databaseBusy) return;
        databaseBusy = true;
        setControlsEnabled(false);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception {
                return task.call();
            }

            @Override protected void done() {
                databaseBusy = false;
                setControlsEnabled(true);
                try {
                    completed.accept(get());
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    failed.accept(exception);
                } catch (ExecutionException exception) {
                    failed.accept(exception.getCause() == null ? exception : exception.getCause());
                }
            }
        }.execute();
    }

    private void setControlsEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        updateButton.setEnabled(enabled);
        paymentButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        billIdField.setEnabled(enabled);
        patientIdField.setEnabled(enabled);
        admissionIdField.setEnabled(enabled);
        roomChargesField.setEnabled(enabled);
        doctorChargesField.setEnabled(enabled);
        medicineChargesField.setEnabled(enabled);
        otherChargesField.setEnabled(enabled);
        paidAmountField.setEnabled(enabled);
        paymentStatusCombo.setEnabled(enabled);
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    private String databaseErrorMessage(Throwable error, String operation) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof IllegalArgumentException && cause.getMessage() != null) {
                return cause.getMessage();
            }
            if (cause instanceof java.sql.SQLException sqlException) {
                String state = sqlException.getSQLState();
                if ("23503".equals(state)) {
                    return "The patient or admission reference is invalid. Confirm the patient exists and that the admission belongs to that patient.";
                }
                if ("23502".equals(state)) {
                    return "A required bill field is missing. Review the patient and charge values.";
                }
                if ("22003".equals(state) || "22001".equals(state)) {
                    return "One or more values exceed the database's supported range.";
                }
                if (state != null && state.startsWith("08")) {
                    return "PostgreSQL could not be reached. Check the database service and connection settings.";
                }
                break;
            }
        }
        return "Unable to " + operation + ". Check the selected records and PostgreSQL connection, then retry.";
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

        Object admissionId = tableModel.getValueAt(row, 2);
        admissionIdField.setText(admissionId == null ? "" : String.valueOf(admissionId));

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
