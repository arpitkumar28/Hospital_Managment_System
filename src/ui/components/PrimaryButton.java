package ui.components;

import ui.theme.AppTheme;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Cursor;

/** Standard primary action button. */
public class PrimaryButton extends JButton {
    public PrimaryButton(String text) {
        super(text);
        setFont(AppTheme.FONT_BUTTON);
        setForeground(AppTheme.SURFACE_COLOR);
        setBackground(AppTheme.PRIMARY_COLOR);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));
    }
}
