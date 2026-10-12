package ui.components;

import ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Cursor;

/** Standard primary action button. */
public class PrimaryButton extends JButton {
    public PrimaryButton(String text) {
        super(text);
        setFont(AppTheme.FONT_BUTTON);
        setForeground(AppTheme.SURFACE_COLOR);
        setBackground(AppTheme.PRIMARY_COLOR);
        setOpaque(true);
        setFocusPainted(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));
        setRolloverEnabled(true);
        setBorderPainted(true);
        setFocusable(true);
        Color base = AppTheme.PRIMARY_COLOR;
        Color hover = new Color(36, 82, 121);
        Color pressed = new Color(17, 47, 77);
        addChangeListener(event -> {
            if (!isEnabled()) {
                setBackground(new Color(190, 202, 212));
                setForeground(AppTheme.TEXT_SECONDARY);
            } else if (getModel().isPressed()) {
                setBackground(pressed);
                setForeground(AppTheme.SURFACE_COLOR);
            } else if (getModel().isRollover()) {
                setBackground(hover);
                setForeground(AppTheme.SURFACE_COLOR);
            } else {
                setBackground(base);
                setForeground(AppTheme.SURFACE_COLOR);
            }
        });
    }
}
