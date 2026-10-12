package ui.theme;

import com.formdev.flatlaf.ui.FlatOptionPaneUI;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** Keeps standard JOptionPane actions readable without recoloring application buttons. */
public final class ContrastOptionPaneUI extends FlatOptionPaneUI {
    private static final Color BUTTON_BACKGROUND = new Color(236, 243, 247);
    private static final Color BUTTON_HOVER = new Color(221, 237, 245);
    private static final Color BUTTON_PRESSED = new Color(205, 225, 235);
    private static final Color BUTTON_DISABLED = new Color(242, 245, 247);
    private static final Color BUTTON_DISABLED_FOREGROUND = new Color(82, 96, 107);

    public static ComponentUI createUI(JComponent component) {
        return new ContrastOptionPaneUI();
    }

    @Override
    protected Container createButtonArea() {
        Container area = super.createButtonArea();
        styleButtons(area);
        return area;
    }

    private static void styleButtons(Component component) {
        if (component instanceof AbstractButton button) {
            styleButton(button);
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                styleButtons(child);
            }
        }
    }

    private static void styleButton(AbstractButton button) {
        button.setFont(AppTheme.FONT_BUTTON);
        button.setForeground(AppTheme.TEXT_PRIMARY);
        button.setBackground(BUTTON_BACKGROUND);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(true);
        button.setBorderPainted(true);
        button.setFocusable(true);
        button.setRolloverEnabled(true);
        button.setMargin(new Insets(7, 16, 7, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateBorder(button, button.hasFocus());
        button.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                updateBorder(button, true);
            }

            @Override
            public void focusLost(FocusEvent event) {
                updateBorder(button, false);
            }
        });
        button.addChangeListener(event -> {
            if (!button.isEnabled()) {
                button.setBackground(BUTTON_DISABLED);
                button.setForeground(BUTTON_DISABLED_FOREGROUND);
            } else {
                button.setForeground(AppTheme.TEXT_PRIMARY);
                if (button.getModel().isPressed()) {
                    button.setBackground(BUTTON_PRESSED);
                } else if (button.getModel().isRollover()) {
                    button.setBackground(BUTTON_HOVER);
                } else {
                    button.setBackground(BUTTON_BACKGROUND);
                }
            }
        });
    }

    private static void updateBorder(AbstractButton button, boolean focused) {
        Color border = focused ? AppTheme.ACCENT_COLOR : AppTheme.BORDER_COLOR;
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                BorderFactory.createEmptyBorder(5, 11, 5, 11)));
    }
}
