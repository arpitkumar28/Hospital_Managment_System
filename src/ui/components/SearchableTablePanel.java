package ui.components;

import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Search, sort, refresh, and empty-state behavior shared by CRUD tables. */
public final class SearchableTablePanel extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchableTablePanel.class);
    private final JTable table;
    private final JLabel resultSummary = new JLabel(" ");
    private final JTextField searchField = new JTextField(22);
    private final TableRowSorter<TableModel> sorter;
    private final JButton refreshButton;
    private String statusOverride;
    private boolean loading;
    private Runnable pendingReload;

    private SearchableTablePanel(JTable table, JScrollPane scrollPane, String searchHint,
                                 Runnable refreshAction, int[] searchableColumns) {
        super(new BorderLayout(AppTheme.SPACE_SM, AppTheme.SPACE_SM));
        this.table = table;
        sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        JPanel toolbar = new JPanel(new BorderLayout(AppTheme.SPACE_SM, 0));
        toolbar.setOpaque(false);
        searchField.putClientProperty("JTextField.placeholderText", searchHint);
        searchField.getAccessibleContext().setAccessibleName("Search table records");
        searchField.setToolTipText(searchHint);
        searchField.setFont(AppTheme.FONT_BODY);
        searchField.setPreferredSize(new java.awt.Dimension(300, 40));
        JLabel searchLabel = new JLabel("Search");
        searchLabel.setLabelFor(searchField);
        searchLabel.setFont(AppTheme.FONT_BODY.deriveFont(java.awt.Font.BOLD));
        JPanel searchGroup = new JPanel(new BorderLayout(AppTheme.SPACE_SM, 0));
        searchGroup.setOpaque(false);
        searchGroup.add(searchLabel, BorderLayout.WEST);
        searchGroup.add(searchField, BorderLayout.CENTER);
        toolbar.add(searchGroup, BorderLayout.CENTER);
        if (refreshAction != null) {
            refreshButton = new JButton("Refresh");
            refreshButton.setFont(AppTheme.FONT_BUTTON);
            refreshButton.setToolTipText("Reload records from the database");
            refreshButton.getAccessibleContext().setAccessibleName("Refresh table records");
            refreshButton.setPreferredSize(new java.awt.Dimension(112, 40));
            refreshButton.addActionListener(event -> refreshAction.run());
            toolbar.add(refreshButton, BorderLayout.EAST);
        } else {
            refreshButton = null;
        }

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { applySearch(searchableColumns); }
            @Override public void removeUpdate(DocumentEvent event) { applySearch(searchableColumns); }
            @Override public void changedUpdate(DocumentEvent event) { applySearch(searchableColumns); }
        });
        table.getModel().addTableModelListener(event -> updateSummary());
        sorter.addRowSorterListener(event -> updateSummary());
        resultSummary.setFont(AppTheme.FONT_SMALL);
        resultSummary.setForeground(AppTheme.TEXT_SECONDARY);
        resultSummary.setBorder(BorderFactory.createEmptyBorder(0, AppTheme.SPACE_XS, 0, 0));
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        footer.setOpaque(false);
        footer.add(resultSummary);

        setBackground(AppTheme.BACKGROUND_COLOR);
        add(toolbar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        updateSummary();
    }

    public static SearchableTablePanel wrap(JTable table, JScrollPane scrollPane, String searchHint,
                                            Runnable refreshAction, int... searchableColumns) {
        return new SearchableTablePanel(table, scrollPane, searchHint, refreshAction, searchableColumns.clone());
    }

    public void setLoading(String message) {
        statusOverride = message;
        loading = true;
        resultSummary.setForeground(AppTheme.INFO_COLOR);
        resultSummary.setText(message);
        if (refreshButton != null) refreshButton.setEnabled(false);
        searchField.setEnabled(false);
    }

    public void setLoaded() {
        statusOverride = null;
        loading = false;
        resultSummary.setForeground(AppTheme.TEXT_SECONDARY);
        if (refreshButton != null) refreshButton.setEnabled(true);
        searchField.setEnabled(true);
        updateSummary();
    }

    public void setError(String message) {
        statusOverride = message;
        loading = false;
        resultSummary.setForeground(AppTheme.ERROR_COLOR);
        resultSummary.setText(message);
        if (refreshButton != null) refreshButton.setEnabled(true);
        searchField.setEnabled(true);
    }

    public <T> void loadAsync(String loadingMessage, String errorMessage,
                              Callable<T> load, Consumer<T> apply) {
        if (loading) {
            pendingReload = () -> loadAsync(loadingMessage, errorMessage, load, apply);
            return;
        }
        loading = true;
        setLoading(loadingMessage);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception {
                return load.call();
            }

            @Override protected void done() {
                loading = false;
                try {
                    T result = get();
                    apply.accept(result);
                    setLoaded();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    LOGGER.warn("Table loading was interrupted.", exception);
                    setError(errorMessage);
                } catch (ExecutionException | RuntimeException exception) {
                    LOGGER.error("Unable to load table data.", exception);
                    setError(errorMessage);
                } finally {
                    if (pendingReload != null) {
                        Runnable reload = pendingReload;
                        pendingReload = null;
                        reload.run();
                    }
                }
            }
        }.execute();
    }

    private void applySearch(int[] columns) {
        String query = searchField.getText().trim();
        sorter.setRowFilter(query.isEmpty() ? null
                : RowFilter.regexFilter("(?iu)" + Pattern.quote(query), columns));
        updateSummary();
    }

    private void updateSummary() {
        if (statusOverride != null) {
            resultSummary.setText(statusOverride);
            return;
        }
        resultSummary.setForeground(AppTheme.TEXT_SECONDARY);
        int total = table.getModel().getRowCount();
        int shown = table.getRowCount();
        String query = searchField.getText().trim();
        if (total == 0) {
            resultSummary.setText("No records to display.");
        } else if (shown == 0) {
            resultSummary.setText("No records match your search.");
        } else if (query.isEmpty()) {
            resultSummary.setText(total + (total == 1 ? " record" : " records"));
        } else {
            resultSummary.setText("Showing " + shown + " of " + total + " records");
        }
    }
}
