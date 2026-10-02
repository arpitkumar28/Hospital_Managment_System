package ui;

import service.UserService;
import ui.components.AuthImagePanel;
import ui.components.PrimaryButton;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Registration request form. New staff accounts require administrator approval. */
public class RegistrationFrame extends JFrame {
    private final JTextField fullName = new JTextField();
    private final JTextField username = new JTextField();
    private final JTextField email = new JTextField();
    private final JTextField phone = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JPasswordField confirm = new JPasswordField();
    private final PrimaryButton submit = new PrimaryButton("Submit registration");
    private final JLabel status = new JLabel(" ");
    private final UserService userService = new UserService();

    public RegistrationFrame() {
        setTitle("Hospital Management System | Request access");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(880, 650));
        setSize(1100, 760);
        setLocationRelativeTo(null);
        setContentPane(createContent());
        getRootPane().setDefaultButton(submit);
        setVisible(true);
    }

    private JPanel createContent() {
        JPanel split = new JPanel(new BorderLayout());
        AuthImagePanel visual = new AuthImagePanel("/assets/hero-hospital-building.png",
                "A connected hospital workspace.", "Request staff access to the Hospital Management System desktop application.");
        visual.setPreferredSize(new Dimension(440, 740));
        split.add(visual, BorderLayout.WEST);

        JPanel scroller = new JPanel(new GridBagLayout());
        scroller.setBackground(AppTheme.SURFACE_COLOR);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(430, 650));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        addHeading(form, c, "Request access", AppTheme.FONT_TITLE, AppTheme.TEXT_PRIMARY, 0, 5);
        addHeading(form, c, "Your account will stay pending until an administrator approves it.",
                AppTheme.FONT_BODY, AppTheme.TEXT_SECONDARY, 0, 10);
        addField(form, c, "Full name", fullName);
        addField(form, c, "Username", username);
        addField(form, c, "Email", email);
        addField(form, c, "Phone", phone);
        addField(form, c, "Password", password);
        addField(form, c, "Confirm password", confirm);
        addHeading(form, c, "Use at least 8 characters with uppercase, lowercase, a number and a symbol.",
                AppTheme.FONT_SMALL, AppTheme.TEXT_SECONDARY, 0, 8);

        c.gridy++;
        c.insets = new Insets(8, 0, 6, 0);
        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);
        javax.swing.JCheckBox show = new javax.swing.JCheckBox("Show passwords");
        show.setOpaque(false);
        show.setFont(AppTheme.FONT_SMALL);
        show.setForeground(AppTheme.TEXT_SECONDARY);
        show.addActionListener(event -> {
            char echo = show.isSelected() ? '\0' : defaultEchoChar();
            password.setEchoChar(echo);
            confirm.setEchoChar(echo);
        });
        controls.add(show, BorderLayout.WEST);
        form.add(controls, c);

        c.gridy++;
        c.insets = new Insets(6, 0, 6, 0);
        submit.setPreferredSize(new Dimension(0, 46));
        submit.addActionListener(event -> register());
        form.add(submit, c);
        c.gridy++;
        status.setFont(AppTheme.FONT_SMALL);
        status.setForeground(AppTheme.ERROR_COLOR);
        form.add(status, c);
        c.gridy++;
        c.insets = new Insets(5, 0, 0, 0);
        JButton back = new JButton("Back to sign in");
        back.setFont(AppTheme.FONT_BODY.deriveFont(java.awt.Font.BOLD));
        back.setForeground(AppTheme.SECONDARY_COLOR);
        back.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 3));
        back.setContentAreaFilled(false);
        back.addActionListener(event -> { dispose(); new LoginFrame(); });
        form.add(back, c);
        scroller.add(form);
        JScrollPane scroll = new JScrollPane(scroller);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        split.add(scroll, BorderLayout.CENTER);
        return split;
    }

    private void addHeading(JPanel panel, GridBagConstraints c, String text,
                            java.awt.Font font, Color color, int top, int bottom) {
        JLabel label = new JLabel("<html><div style='width:410px'>" + text + "</div></html>");
        label.setFont(font);
        label.setForeground(color);
        c.gridy++;
        c.insets = new Insets(top, 0, bottom, 0);
        panel.add(label, c);
    }

    private void addField(JPanel panel, GridBagConstraints c, String labelText, JTextField field) {
        c.gridy++;
        c.insets = new Insets(5, 0, 4, 0);
        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.FONT_BODY.deriveFont(java.awt.Font.BOLD));
        label.setForeground(AppTheme.TEXT_PRIMARY);
        panel.add(label, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 5, 0);
        field.setFont(AppTheme.FONT_BODY);
        field.setPreferredSize(new Dimension(0, 39));
        panel.add(field, c);
    }

    private char defaultEchoChar() {
        Object value = javax.swing.UIManager.get("PasswordField.echoChar");
        return value instanceof Character character ? character : '\u2022';
    }

    private void register() {
        char[] passwordChars = password.getPassword();
        char[] confirmChars = confirm.getPassword();
        String name = fullName.getText();
        String login = username.getText();
        String mail = email.getText();
        String contact = phone.getText();
        submit.setEnabled(false);
        status.setForeground(AppTheme.TEXT_SECONDARY);
        status.setText("Submitting…");
        new SwingWorker<Void, Void>() {
            private String error;
            @Override protected Void doInBackground() {
                try { userService.register(name, login, mail, contact, passwordChars, confirmChars); }
                catch (UserService.RegistrationException exception) { error = exception.getMessage(); }
                return null;
            }
            @Override protected void done() {
                submit.setEnabled(true);
                if (error == null) {
                    JOptionPane.showMessageDialog(RegistrationFrame.this,
                            "Your registration request has been submitted. Contact an administrator for account approval.",
                            "Request submitted", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new LoginFrame();
                } else {
                    status.setForeground(AppTheme.ERROR_COLOR);
                    status.setText("<html>" + error.replace("\n", "<br>") + "</html>");
                    password.setText("");
                    confirm.setText("");
                }
            }
        }.execute();
    }
}
