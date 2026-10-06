package ui;

import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

/** Honest status page while report queries/export are not supported by the backend. */
public final class ReportsPanel extends JFrame {
    public ReportsPanel() {
        super("Hospital Management System - Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 480));
        setSize(900, 560);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(AppTheme.SPACE_MD, AppTheme.SPACE_MD));
        root.setBackground(AppTheme.BACKGROUND_COLOR);
        root.setBorder(AppTheme.pagePadding());

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new javax.swing.BoxLayout(heading, javax.swing.BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Reports");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Operational and financial summaries");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        heading.add(title);
        heading.add(javax.swing.Box.createVerticalStrut(AppTheme.SPACE_XS));
        heading.add(subtitle);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.SURFACE_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(AppTheme.SPACE_XL, AppTheme.SPACE_XL,
                        AppTheme.SPACE_XL, AppTheme.SPACE_XL)));
        JPanel message = new JPanel();
        message.setOpaque(false);
        message.setLayout(new javax.swing.BoxLayout(message, javax.swing.BoxLayout.Y_AXIS));
        JLabel unavailable = new JLabel("Report generation is not available yet.");
        unavailable.setFont(AppTheme.FONT_HEADING);
        unavailable.setForeground(AppTheme.TEXT_PRIMARY);
        unavailable.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        JLabel detail = new JLabel("This build does not include reporting queries or exports.");
        detail.setFont(AppTheme.FONT_BODY);
        detail.setForeground(AppTheme.TEXT_SECONDARY);
        detail.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        message.add(unavailable);
        message.add(javax.swing.Box.createVerticalStrut(AppTheme.SPACE_SM));
        message.add(detail);
        card.add(message);

        root.add(heading, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        setVisible(true);
    }
}
