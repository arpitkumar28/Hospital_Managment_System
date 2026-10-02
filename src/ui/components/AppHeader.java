package ui.components;

import ui.theme.AppTheme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;

/** Standard application page header. */
public class AppHeader extends JPanel {
    public AppHeader(String title, String userName, String role) {
        super(new BorderLayout());
        setBackground(AppTheme.SURFACE_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)));
        JLabel heading = new JLabel(title);
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel user = new JLabel(userName + "  •  " + role);
        user.setFont(AppTheme.FONT_BODY);
        user.setForeground(AppTheme.TEXT_SECONDARY);
        add(heading, BorderLayout.WEST);
        add(user, BorderLayout.EAST);
    }
}
