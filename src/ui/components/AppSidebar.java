package ui.components;

import ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Reusable dark navigation sidebar with a selected item and logout action. */
public class AppSidebar extends JPanel {
    private final Map<String, JButton> navigationButtons = new LinkedHashMap<>();
    private final JPanel navigation = new JPanel();
    private final Consumer<String> selectionHandler;
    private JButton selectedButton;

    public AppSidebar(Consumer<String> selectionHandler, Runnable logoutHandler) {
        super(new BorderLayout());
        this.selectionHandler = selectionHandler;
        setBackground(AppTheme.SIDEBAR_COLOR);
        setPreferredSize(new Dimension(264, 600));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(22, 20, 20, 16));
        JLabel mark = new JLabel("+");
        mark.setOpaque(true);
        mark.setBackground(AppTheme.ACCENT_COLOR);
        mark.setForeground(Color.WHITE);
        mark.setFont(AppTheme.FONT_TITLE.deriveFont(java.awt.Font.BOLD, 25f));
        mark.setHorizontalAlignment(JLabel.CENTER);
        mark.setPreferredSize(new Dimension(42, 42));
        JPanel brandCopy = new JPanel(new BorderLayout(0, 3));
        brandCopy.setOpaque(false);
        JLabel name = new JLabel("Hospital Management");
        name.setForeground(Color.WHITE);
        name.setFont(AppTheme.FONT_SUBHEADING.deriveFont(java.awt.Font.BOLD));
        JLabel caption = new JLabel("CARE OPERATIONS");
        caption.setForeground(new Color(171, 196, 204));
        caption.setFont(AppTheme.FONT_SMALL.deriveFont(java.awt.Font.BOLD, 9f));
        brandCopy.add(name, BorderLayout.CENTER);
        brandCopy.add(caption, BorderLayout.SOUTH);
        brand.add(mark, BorderLayout.WEST);
        brand.add(brandCopy, BorderLayout.CENTER);

        navigation.setOpaque(false);
        navigation.setLayout(new javax.swing.BoxLayout(navigation, javax.swing.BoxLayout.Y_AXIS));
        navigation.setBorder(BorderFactory.createEmptyBorder(8, 14, 16, 14));
        JScrollPane navigationScroll = new JScrollPane(navigation,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        navigationScroll.setBorder(BorderFactory.createEmptyBorder());
        navigationScroll.setOpaque(false);
        navigationScroll.getViewport().setOpaque(false);
        navigationScroll.getVerticalScrollBar().setUnitIncrement(16);

        JButton logout = createButton("Logout");
        logout.setForeground(new Color(255, 215, 215));
        logout.addActionListener(event -> logoutHandler.run());
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(44, 72, 85)),
                BorderFactory.createEmptyBorder(12, 12, 14, 12)));
        footer.add(logout);

        add(brand, BorderLayout.NORTH);
        add(navigationScroll, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    public void addNavigationItem(String label) {
        JButton button = createButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(event -> {
            setSelected(label);
            selectionHandler.accept(label);
        });
        navigationButtons.put(label, button);
        navigation.add(button);
        navigation.add(javax.swing.Box.createVerticalStrut(8));
    }

    public void setSelected(String label) {
        if (selectedButton != null) {
            styleButton(selectedButton, false);
        }
        selectedButton = navigationButtons.get(label);
        if (selectedButton != null) {
            styleButton(selectedButton, true);
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(AppTheme.FONT_BODY.deriveFont(java.awt.Font.BOLD));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFocusPainted(true);
        button.setFocusable(true);
        button.setBorder(BorderFactory.createEmptyBorder(11, 14, 11, 12));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.getAccessibleContext().setAccessibleName(text);
        styleButton(button, false);
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent event) {
                if (button != selectedButton) button.setBackground(new Color(37, 76, 93));
            }
            @Override public void mouseExited(MouseEvent event) {
                if (button != selectedButton) styleButton(button, false);
            }
        });
        return button;
    }

    private void styleButton(JButton button, boolean selected) {
        button.setOpaque(true);
        button.setBackground(selected ? new Color(35, 84, 99) : AppTheme.SIDEBAR_COLOR);
        button.setForeground(selected ? Color.WHITE : new Color(218, 231, 234));
        button.setBorder(BorderFactory.createCompoundBorder(
                selected ? BorderFactory.createMatteBorder(0, 3, 0, 0, AppTheme.ACCENT_COLOR)
                        : BorderFactory.createEmptyBorder(0, 3, 0, 0),
                BorderFactory.createEmptyBorder(11, 11, 11, 12)));
    }
}
