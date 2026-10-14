package ui.components;

import ui.theme.AppTheme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;

/** Standard application page header. */
public class AppHeader extends JPanel {
    public AppHeader(String title, String userName, String role) {
        super(new BorderLayout());
        setBackground(AppTheme.SURFACE_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 24, 12, 24)));
        setPreferredSize(new Dimension(0, 76));

        JPanel identity = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        identity.setOpaque(false);
        JLabel mark = new JLabel("+");
        mark.setHorizontalAlignment(JLabel.CENTER);
        mark.setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 25f));
        mark.setForeground(Color.WHITE);
        mark.setOpaque(true);
        mark.setBackground(AppTheme.ACCENT_COLOR);
        mark.setPreferredSize(new Dimension(42, 42));
        mark.setBorder(BorderFactory.createLineBorder(AppTheme.ACCENT_COLOR, 1, true));

        JPanel titleBlock = new JPanel(new BorderLayout(0, 3));
        titleBlock.setOpaque(false);
        JLabel heading = new JLabel(title);
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel context = new JLabel("HOSPITAL OPERATIONS");
        context.setFont(AppTheme.FONT_SMALL.deriveFont(Font.BOLD, 9f));
        context.setForeground(AppTheme.TEXT_SECONDARY);
        titleBlock.add(heading, BorderLayout.CENTER);
        titleBlock.add(context, BorderLayout.SOUTH);
        identity.add(mark);
        identity.add(titleBlock);

        JPanel account = new JPanel(new BorderLayout(12, 0));
        account.setOpaque(false);
        JPanel copy = new JPanel(new BorderLayout(0, 3));
        copy.setOpaque(false);
        JLabel signedIn = new JLabel("SIGNED IN");
        signedIn.setFont(AppTheme.FONT_SMALL.deriveFont(Font.BOLD, 9f));
        signedIn.setForeground(AppTheme.TEXT_SECONDARY);
        JLabel userNameLabel = new JLabel(userName);
        userNameLabel.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
        userNameLabel.setForeground(AppTheme.TEXT_PRIMARY);
        userNameLabel.setToolTipText(userName);
        copy.add(signedIn, BorderLayout.NORTH);
        copy.add(userNameLabel, BorderLayout.SOUTH);

        JLabel roleBadge = new JLabel(role.replace('_', ' '));
        roleBadge.setFont(AppTheme.FONT_SMALL.deriveFont(Font.BOLD));
        roleBadge.setForeground(AppTheme.SECONDARY_COLOR);
        roleBadge.setOpaque(true);
        roleBadge.setBackground(new Color(231, 244, 243));
        roleBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(207, 231, 229)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        account.add(copy, BorderLayout.CENTER);
        account.add(roleBadge, BorderLayout.EAST);

        add(identity, BorderLayout.WEST);
        add(account, BorderLayout.EAST);
    }
}
