package ui;

import model.BillingReport;
import model.BillingReport.PaymentStatusTotal;
import model.BillingReport.RevenueTotal;
import service.ReportService;
import ui.components.PrimaryButton;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/** Date-filtered reports backed by PostgreSQL billing data. */
public final class ReportsPanel extends JFrame {
    private final ReportService reportService = new ReportService();
    private final JTextField fromField = new JTextField(10);
    private final JTextField toField = new JTextField(10);
    private final JButton refreshButton = new PrimaryButton("Run report");
    private final JButton exportButton = new JButton("Export CSV");
    private final JLabel status = new JLabel("Enter a date range and run a report.");
    private final JLabel[] summaryValues = {
            new JLabel("—"), new JLabel("—"), new JLabel("—"), new JLabel("—")
    };
    private final DefaultTableModel statusModel = tableModel(
            "Payment status", "Bills", "Billed", "Collected", "Outstanding");
    private final DefaultTableModel dailyModel = tableModel("Date", "Collected");
    private final DefaultTableModel monthlyModel = tableModel("Month", "Collected");
    private BillingReport currentReport;
    private boolean loading;
    private boolean exporting;

    public ReportsPanel() {
        super("Hospital Management System - Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(860, 600));
        setSize(1100, 720);
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
        refreshReport();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(AppTheme.SPACE_MD, AppTheme.SPACE_MD));
        root.setBackground(AppTheme.BACKGROUND_COLOR);
        root.setBorder(AppTheme.pagePadding());

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new javax.swing.BoxLayout(heading, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Financial Reports");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Billing totals and collected amounts from PostgreSQL");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        heading.add(title);
        heading.add(javax.swing.Box.createVerticalStrut(AppTheme.SPACE_XS));
        heading.add(subtitle);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, AppTheme.SPACE_SM, AppTheme.SPACE_SM));
        filters.setBackground(AppTheme.SURFACE_COLOR);
        filters.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(AppTheme.SPACE_SM, AppTheme.SPACE_SM,
                        AppTheme.SPACE_SM, AppTheme.SPACE_SM)));
        filters.add(new JLabel("From (YYYY-MM-DD)"));
        filters.add(fromField);
        filters.add(new JLabel("Through"));
        filters.add(toField);
        filters.add(refreshButton);
        filters.add(exportButton);
        refreshButton.addActionListener(event -> refreshReport());
        exportButton.setEnabled(false);
        exportButton.addActionListener(event -> chooseExportPath());

        JPanel top = new JPanel(new BorderLayout(0, AppTheme.SPACE_MD));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(filters, BorderLayout.CENTER);

        JPanel summary = new JPanel(new GridLayout(1, 4, AppTheme.SPACE_MD, 0));
        summary.setOpaque(false);
        String[] labels = {"Bills", "Total billed", "Collected", "Outstanding"};
        for (int i = 0; i < summaryValues.length; i++) {
            summary.add(summaryCard(labels[i], summaryValues[i]));
        }

        JTabbedPane breakdown = new JTabbedPane();
        breakdown.addTab("Payment status", table(statusModel));
        breakdown.addTab("Daily collected", table(dailyModel));
        breakdown.addTab("Monthly collected", table(monthlyModel));

        JPanel content = new JPanel(new BorderLayout(AppTheme.SPACE_MD, AppTheme.SPACE_MD));
        content.setOpaque(false);
        content.add(summary, BorderLayout.NORTH);
        content.add(breakdown, BorderLayout.CENTER);
        status.setFont(AppTheme.FONT_SMALL);
        status.setForeground(AppTheme.TEXT_SECONDARY);
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(status, BorderLayout.WEST);
        JLabel attribution = new JLabel("Collected amounts are cumulative payments attributed to bill date.");
        attribution.setFont(AppTheme.FONT_SMALL);
        attribution.setForeground(AppTheme.TEXT_SECONDARY);
        footer.add(attribution, BorderLayout.EAST);
        content.add(footer, BorderLayout.SOUTH);

        root.add(top, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
        LocalDate today = LocalDate.now();
        fromField.setText(today.withDayOfMonth(1).toString());
        toField.setText(today.toString());
    }

    private JPanel summaryCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(AppTheme.SPACE_SM, AppTheme.SPACE_SM));
        card.setBackground(AppTheme.SURFACE_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(AppTheme.SPACE_MD, AppTheme.SPACE_MD,
                        AppTheme.SPACE_MD, AppTheme.SPACE_MD)));
        JLabel label = new JLabel(title);
        label.setFont(AppTheme.FONT_SMALL);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        value.setFont(AppTheme.FONT_HEADING);
        value.setForeground(AppTheme.PRIMARY_COLOR);
        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private JScrollPane table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(34);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        return new JScrollPane(table);
    }

    private void refreshReport() {
        if (loading || exporting) return;
        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(fromField.getText().trim());
            to = LocalDate.parse(toField.getText().trim());
            if (from.isAfter(to)) throw new IllegalArgumentException("Start date must not be after end date.");
        } catch (IllegalArgumentException exception) {
            status.setText("Enter a valid date range using YYYY-MM-DD.");
            status.setForeground(AppTheme.ERROR_COLOR);
            return;
        }

        loading = true;
        clearReportData();
        refreshButton.setEnabled(false);
        exportButton.setEnabled(false);
        status.setText("Loading report…");
        status.setForeground(AppTheme.TEXT_SECONDARY);
        new SwingWorker<BillingReport, Void>() {
            @Override protected BillingReport doInBackground() {
                return reportService.getBillingReport(from, to);
            }

            @Override protected void done() {
                loading = false;
                refreshButton.setEnabled(true);
                try {
                    currentReport = get();
                    displayReport(currentReport);
                    exportButton.setEnabled(true);
                    status.setText(currentReport.billCount() == 0 ? "No bills were found in this date range."
                            : "Report refreshed.");
                    status.setForeground(AppTheme.SUCCESS_COLOR);
                } catch (Exception exception) {
                    currentReport = null;
                    clearReportData();
                    exportButton.setEnabled(false);
                    status.setText("Unable to load report. Check the PostgreSQL connection and date range.");
                    status.setForeground(AppTheme.ERROR_COLOR);
                    org.slf4j.LoggerFactory.getLogger(ReportsPanel.class)
                            .error("Unable to load financial report.", exception);
                    JOptionPane.showMessageDialog(ReportsPanel.this, status.getText(),
                            "Report error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void displayReport(BillingReport report) {
        summaryValues[0].setText(Long.toString(report.billCount()));
        summaryValues[1].setText(money(report.billedAmount()));
        summaryValues[2].setText(money(report.collectedAmount()));
        summaryValues[3].setText(money(report.outstandingAmount()));
        replaceRows(statusModel, report.paymentStatusTotals().stream()
                .map(total -> new Object[]{total.status(), total.billCount(), money(total.billedAmount()),
                        money(total.collectedAmount()), money(total.outstandingAmount())}).toList());
        replaceRows(dailyModel, revenueRows(report.dailyRevenue()));
        replaceRows(monthlyModel, revenueRows(report.monthlyRevenue()));
    }

    private List<Object[]> revenueRows(List<RevenueTotal> values) {
        return values.stream().map(value -> new Object[]{value.period(), money(value.collectedAmount())}).toList();
    }

    private void chooseExportPath() {
        if (currentReport == null || loading || exporting) return;
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("billing-report-" + currentReport.from()
                + "-to-" + currentReport.to() + ".csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path path = chooser.getSelectedFile().toPath();
        exporting = true;
        exportButton.setEnabled(false);
        refreshButton.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() throws IOException {
                Files.writeString(path, toCsv(currentReport), StandardCharsets.UTF_8);
                return null;
            }

            @Override protected void done() {
                exporting = false;
                refreshButton.setEnabled(true);
                exportButton.setEnabled(currentReport != null);
                try {
                    get();
                    status.setText("CSV report exported.");
                    status.setForeground(AppTheme.SUCCESS_COLOR);
                } catch (Exception exception) {
                    org.slf4j.LoggerFactory.getLogger(ReportsPanel.class)
                            .error("Unable to export billing report.", exception);
                    JOptionPane.showMessageDialog(ReportsPanel.this,
                            "Unable to export the CSV file. Check the selected location.",
                            "Export error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    static String toCsv(BillingReport report) {
        StringBuilder csv = new StringBuilder("Billing report,")
                .append(report.from()).append(" through ").append(report.to()).append("\n\n")
                .append("Summary\nMetric,Value\n")
                .append("Bills,").append(report.billCount()).append('\n')
                .append("Total billed,").append(report.billedAmount().toPlainString()).append('\n')
                .append("Collected,").append(report.collectedAmount().toPlainString()).append('\n')
                .append("Outstanding,").append(report.outstandingAmount().toPlainString()).append("\n\n")
                .append("Payment status\nStatus,Bills,Billed,Collected,Outstanding\n");
        for (PaymentStatusTotal value : report.paymentStatusTotals()) {
            csv.append(csvCell(value.status())).append(',').append(value.billCount()).append(',')
                    .append(value.billedAmount().toPlainString()).append(',')
                    .append(value.collectedAmount().toPlainString()).append(',')
                    .append(value.outstandingAmount().toPlainString()).append('\n');
        }
        csv.append("\nDaily collected\nDate,Collected\n");
        appendRevenue(csv, report.dailyRevenue());
        csv.append("\nMonthly collected\nMonth,Collected\n");
        appendRevenue(csv, report.monthlyRevenue());
        csv.append("\nCollected amounts are cumulative payments attributed to bill date.\n");
        return csv.toString();
    }

    private static void appendRevenue(StringBuilder csv, List<RevenueTotal> values) {
        for (RevenueTotal value : values) {
            csv.append(value.period()).append(',').append(value.collectedAmount().toPlainString()).append('\n');
        }
    }

    private static String csvCell(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private static String money(java.math.BigDecimal value) {
        return "₹" + value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private void clearReportData() {
        for (JLabel value : summaryValues) value.setText("—");
        statusModel.setRowCount(0);
        dailyModel.setRowCount(0);
        monthlyModel.setRowCount(0);
    }

    private static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private static void replaceRows(DefaultTableModel model, List<Object[]> rows) {
        model.setRowCount(0);
        for (Object[] row : rows) model.addRow(row);
    }
}
