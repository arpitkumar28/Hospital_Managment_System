package ui.components;

import ui.theme.AppTheme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;

/** Reusable dashboard statistic card with a label and live value. */
public class StatCard extends JPanel {
    private final JLabel valueLabel;

    public StatCard(String title, String value, Color accent) {
        super(new BorderLayout(AppTheme.SPACE_MD, AppTheme.SPACE_MD));
        setBackground(AppTheme.SURFACE_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20))));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_SUBHEADING);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        valueLabel = new JLabel(value);
        valueLabel.setFont(AppTheme.FONT_TITLE.deriveFont(34f));
        valueLabel.setForeground(AppTheme.TEXT_PRIMARY);
        add(titleLabel, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
