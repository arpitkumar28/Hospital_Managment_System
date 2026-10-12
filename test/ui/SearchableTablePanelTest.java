package ui;

import org.junit.jupiter.api.Test;
import ui.components.SearchableTablePanel;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchableTablePanelTest {
    @Test
    void filtersRowsAndReportsWhenNothingMatches() throws Exception {
        AtomicReference<String> status = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            DefaultTableModel model = new DefaultTableModel(new Object[][]{
                    {1, "Avery Patient", "555-1000"},
                    {2, "Jordan Patient", "555-2000"}
            }, new Object[]{"ID", "Name", "Phone"});
            JTable table = new JTable(model);
            SearchableTablePanel panel = SearchableTablePanel.wrap(table, new JScrollPane(table),
                    "Search by name or phone", null, 1, 2);
            JTextField search = findSearchField(panel);

            assertEquals(2, table.getRowCount());
            search.setText("jordan");
            assertEquals(1, table.getRowCount());
            assertEquals("Jordan Patient", table.getValueAt(0, 1));

            search.setText("no match");
            assertEquals(0, table.getRowCount());
            status.set(findSummary(panel));
        });
        assertTrue(status.get().contains("No records match"));
    }

    private static JTextField findSearchField(Container root) {
        for (Component child : root.getComponents()) {
            if (child instanceof JTextField field) return field;
            if (child instanceof Container container) {
                try { return findSearchField(container); }
                catch (IllegalStateException ignored) { }
            }
        }
        throw new IllegalStateException("Search field not found");
    }

    private static String findSummary(Container root) {
        for (Component child : root.getComponents()) {
            if (child instanceof javax.swing.JLabel label && label.getText().contains("No records match")) {
                return label.getText();
            }
            if (child instanceof Container container) {
                try { return findSummary(container); }
                catch (IllegalStateException ignored) { }
            }
        }
        throw new IllegalStateException("Summary not found");
    }
}
