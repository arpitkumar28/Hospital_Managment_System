package ui.theme;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JPasswordField;
import javax.swing.table.JTableHeader;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Container;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Insets;

/** Shared visual tokens and Look & Feel configuration for the Swing application. */
public final class AppTheme {

    public static final Color PRIMARY_COLOR = new Color(23, 59, 94);
    public static final Color SECONDARY_COLOR = new Color(36, 107, 138);
    public static final Color ACCENT_COLOR = new Color(17, 132, 145);
    public static final Color BACKGROUND_COLOR = new Color(243, 247, 250);
    public static final Color SURFACE_COLOR = Color.WHITE;
    public static final Color SIDEBAR_COLOR = new Color(18, 39, 62);
    public static final Color TEXT_PRIMARY = new Color(31, 48, 61);
    public static final Color TEXT_SECONDARY = new Color(102, 119, 132);
    public static final Color BORDER_COLOR = new Color(222, 231, 236);
    public static final Color TABLE_ALTERNATE_ROW_COLOR = new Color(248, 251, 252);
    public static final Color TABLE_SELECTION_COLOR = new Color(221, 237, 245);
    public static final Color TABLE_HEADER_COLOR = new Color(236, 243, 247);
    public static final Color SUCCESS_COLOR = new Color(27, 128, 89);
    public static final Color WARNING_COLOR = new Color(181, 112, 20);
    public static final Color ERROR_COLOR = new Color(181, 59, 59);
    public static final Color INFO_COLOR = new Color(40, 112, 166);

    public static final int SPACE_XS = 4;
    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 16;
    public static final int SPACE_LG = 24;
    public static final int SPACE_XL = 32;

    public static final int RADIUS_SM = 6;
    public static final int RADIUS_MD = 10;
    public static final int RADIUS_LG = 16;

    public static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font(Font.SANS_SERIF, Font.BOLD, 19);
    public static final Font FONT_SUBHEADING = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font FONT_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    private AppTheme() { }

    /** Installs the application's FlatLaf theme before Swing components are created. */
    public static void install() {
        FlatLightLaf.setup();
        UIManager.put("defaultFont", FONT_BODY);
        UIManager.put("OptionPaneUI", ContrastOptionPaneUI.class.getName());
        UIManager.put("OptionPane.background", BACKGROUND_COLOR);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("OptionPane.buttonPadding", 8);
        UIManager.put("OptionPane.buttonMinimumWidth", 84);
        UIManager.put("Component.arc", RADIUS_SM);
        UIManager.put("Button.arc", RADIUS_SM);
        UIManager.put("TextComponent.arc", RADIUS_SM);
        UIManager.put("Component.focusColor", ACCENT_COLOR);
        UIManager.put("Component.borderColor", BORDER_COLOR);
        UIManager.put("Button.default.background", PRIMARY_COLOR);
        UIManager.put("Button.default.foreground", SURFACE_COLOR);
        UIManager.put("Table.rowHeight", 40);
        UIManager.put("Table.showHorizontalLines", false);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.selectionBackground", TABLE_SELECTION_COLOR);
        UIManager.put("Table.selectionForeground", TEXT_PRIMARY);
        UIManager.put("Table.alternateRowColor", TABLE_ALTERNATE_ROW_COLOR);
        UIManager.put("TableHeader.background", TABLE_HEADER_COLOR);
        UIManager.put("TableHeader.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", SURFACE_COLOR);
        UIManager.put("TextField.background", SURFACE_COLOR);
        UIManager.put("PasswordField.background", SURFACE_COLOR);
        UIManager.put("Button.focusedBorderColor", ACCENT_COLOR);
        UIManager.put("Button.hoverBackground", new Color(36, 82, 121));
        UIManager.put("Button.pressedBackground", new Color(17, 47, 77));
        UIManager.put("Button.disabledText", TEXT_SECONDARY);
        UIManager.put("Component.disabledForeground", TEXT_SECONDARY);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("ScrollBar.width", 12);
    }

    /** Applies shared typography, input sizing, and table treatment to legacy CRUD screens. */
    public static void styleLegacyWindow(Container root) {
        styleTree(root);
    }

    private static void styleTree(Component component) {
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                styleTree(child);
            }
        }
        if (!(component instanceof JComponent swing)) return;

        if (component instanceof JPanel && new Color(245, 247, 250).equals(component.getBackground())) {
            component.setBackground(BACKGROUND_COLOR);
        } else if (component instanceof JPanel && (new Color(30, 55, 80).equals(component.getBackground())
                || new Color(34, 65, 94).equals(component.getBackground()))) {
            component.setBackground(SIDEBAR_COLOR);
        }

        if (component instanceof JTable table) {
            table.setFont(FONT_BODY);
            table.setForeground(TEXT_PRIMARY);
            table.setBackground(SURFACE_COLOR);
            table.setSelectionBackground(TABLE_SELECTION_COLOR);
            table.setSelectionForeground(TEXT_PRIMARY);
            table.setRowHeight(Math.max(table.getRowHeight(), 42));
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 0));
            table.setFillsViewportHeight(true);
            table.setGridColor(BORDER_COLOR);
            table.setFocusable(true);
            table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            table.setSelectionBackground(TABLE_SELECTION_COLOR);
            table.setSelectionForeground(TEXT_PRIMARY);
            table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable source, Object value, boolean selected,
                                                               boolean focused, int row, int column) {
                    Component cell = super.getTableCellRendererComponent(
                            source, value, selected, focused, row, column);
                    if (!selected) {
                        cell.setBackground(row % 2 == 0 ? SURFACE_COLOR : TABLE_ALTERNATE_ROW_COLOR);
                        cell.setForeground(TEXT_PRIMARY);
                    }
                    setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                    return cell;
                }
            });
        } else if (component instanceof JTableHeader header) {
            header.setFont(FONT_SUBHEADING);
            header.setForeground(TEXT_PRIMARY);
            header.setBackground(TABLE_HEADER_COLOR);
            header.setPreferredSize(new Dimension(0, 42));
            header.setReorderingAllowed(false);
        } else if (component instanceof JTextField || component instanceof JPasswordField
                || component instanceof JComboBox<?>) {
            component.setFont(FONT_BODY);
            component.setForeground(TEXT_PRIMARY);
            Dimension size = component.getPreferredSize();
            if (size.height < 40) component.setPreferredSize(new Dimension(size.width, 40));
            swing.setFocusable(true);
        } else if (component instanceof AbstractButton button) {
            String label = button.getText() == null ? "" : button.getText().trim().toLowerCase(java.util.Locale.ROOT);
            button.setFont(FONT_BUTTON);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setFocusPainted(true);
            button.setFocusable(true);
            button.setMargin(new Insets(9, 16, 9, 16));
            button.setPreferredSize(new Dimension(Math.max(96, button.getPreferredSize().width), 42));
            if (label.contains("delete") || label.contains("remove")) {
                button.setBackground(ERROR_COLOR);
                button.setForeground(SURFACE_COLOR);
                button.setOpaque(true);
            } else if (label.contains("add") || label.contains("book")
                    || label.contains("admit") || label.contains("discharge")
                    || label.contains("update") || label.contains("save")) {
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(SURFACE_COLOR);
                button.setOpaque(true);
            } else {
                button.setForeground(PRIMARY_COLOR);
            }
        } else if (component instanceof javax.swing.JLabel label) {
            Font current = label.getFont();
            if (current != null && "Arial".equalsIgnoreCase(current.getFamily())) {
                Font replacement = current.getSize() >= 24 ? FONT_TITLE
                        : current.getSize() >= 18 ? FONT_HEADING : FONT_BODY;
                label.setFont(replacement.deriveFont(current.getStyle()));
            }
        }

        if (component instanceof JScrollPane scrollPane) {
            scrollPane.getViewport().setBackground(SURFACE_COLOR);
        }
    }

    public static Border pagePadding() {
        return BorderFactory.createEmptyBorder(SPACE_XL, SPACE_XL, SPACE_XL, SPACE_XL);
    }
}
