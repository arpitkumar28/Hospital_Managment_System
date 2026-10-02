package ui.components;

import ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
        setPreferredSize(new Dimension(238, 600));

        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(25, 20, 24, 16));
        JLabel mark = new JLabel("H");
        mark.setOpaque(true);
        mark.setBackground(AppTheme.ACCENT_COLOR);
        mark.setForeground(Color.WHITE);
        mark.setFont(AppTheme.FONT_HEADING);
        mark.setHorizontalAlignment(JLabel.CENTER);
        mark.setPreferredSize(new Dimension(40, 40));
        JLabel name = new JLabel("  Hospital Management");
        name.setForeground(Color.WHITE);
        name.setFont(AppTheme.FONT_SUBHEADING);
        brand.add(mark, BorderLayout.WEST);
        brand.add(name, BorderLayout.CENTER);

        navigation.setOpaque(false);
        navigation.setLayout(new javax.swing.BoxLayout(navigation, javax.swing.BoxLayout.Y_AXIS));
        navigation.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        JButton logout = createButton("Logout");
        logout.setForeground(new Color(255, 215, 215));
        logout.addActionListener(event -> logoutHandler.run());
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 12, 16, 12));
        footer.add(logout);

        add(brand, BorderLayout.NORTH);
        add(navigation, BorderLayout.CENTER);
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
        navigation.add(javax.swing.Box.createVerticalStrut(5));
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
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 12));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        button.setBackground(selected ? AppTheme.SECONDARY_COLOR : AppTheme.SIDEBAR_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                selected ? BorderFactory.createMatteBorder(0, 3, 0, 0, AppTheme.ACCENT_COLOR)
                        : BorderFactory.createEmptyBorder(0, 3, 0, 0),
                BorderFactory.createEmptyBorder(12, 11, 12, 12)));
    }
}
