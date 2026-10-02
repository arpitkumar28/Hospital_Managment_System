package ui;

import dao.UserDAO;
import ui.components.PrimaryButton;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

/** Sign-in screen for hospital staff. */
public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);
    private final PrimaryButton loginButton = new PrimaryButton("Sign in");

    public LoginFrame() {
        setTitle("Hospital Management System | Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 540));
        setSize(920, 620);
        setLocationRelativeTo(null);
        createUI();
        setVisible(true);
    }

    private void createUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AppTheme.BACKGROUND_COLOR);
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.SURFACE_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(34, 38, 30, 38)));
        card.setPreferredSize(new Dimension(460, 440));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 8, 0);

        JLabel brand = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        brand.setFont(AppTheme.FONT_SMALL.deriveFont(java.awt.Font.BOLD, 12f));
        brand.setForeground(AppTheme.SECONDARY_COLOR);
        card.add(brand, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 6, 0);
        JLabel title = new JLabel("Welcome back");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(title, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 26, 0);
        JLabel subtitle = new JLabel("Sign in to continue to your workspace.");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        card.add(subtitle, c);

        addField(card, c, "Username", usernameField);
        addField(card, c, "Password", passwordField);

        c.gridy++;
        c.insets = new Insets(12, 0, 10, 0);
        loginButton.setPreferredSize(new Dimension(0, 46));
        loginButton.addActionListener(event -> login());
        card.add(loginButton, c);
        getRootPane().setDefaultButton(loginButton);

        c.gridy++;
        c.insets = new Insets(8, 0, 0, 0);
        JLabel footer = new JLabel("Secure staff access  •  Desktop edition", SwingConstants.CENTER);
        footer.setFont(AppTheme.FONT_SMALL);
        footer.setForeground(AppTheme.TEXT_SECONDARY);
        card.add(footer, c);

        root.add(card);
        add(root, BorderLayout.CENTER);
    }

    private void addField(JPanel card, GridBagConstraints c, String labelText, JTextField field) {
        c.gridy++;
        c.insets = new Insets(0, 0, 7, 0);
        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.FONT_BODY.deriveFont(java.awt.Font.BOLD));
        label.setForeground(AppTheme.TEXT_PRIMARY);
        card.add(label, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 18, 0);
        field.setFont(AppTheme.FONT_BODY);
        field.setPreferredSize(new Dimension(0, 42));
        card.add(field, c);
    }

    private void login() {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        if (username.isEmpty() || passwordChars.length == 0) {
            Arrays.fill(passwordChars, '\0');
            JOptionPane.showMessageDialog(this, "Enter your username and password.",
                    "Sign in", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role;
        try {
            role = new UserDAO().login(username, new String(passwordChars));
        } finally {
            Arrays.fill(passwordChars, '\0');
        }

        if (role != null) {
            dispose();
            if (role.equals("ADMIN")) {
                new AdminDashboard();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Your account does not have a supported dashboard role.",
                        "Access unavailable", JOptionPane.WARNING_MESSAGE);
                new LoginFrame();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                    "Sign in failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
