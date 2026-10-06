package ui;

import model.AuthenticatedUser;
import service.AuthService;
import ui.components.AuthImagePanel;
import ui.components.PrimaryButton;
import ui.theme.AppTheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Staff sign-in screen. Database work runs off the Swing event thread. */
public class LoginFrame extends JFrame {
    private final JTextField identifier = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final PrimaryButton signIn = new PrimaryButton("Sign in");
    private final JLabel status = new JLabel(" ");
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Hospital Management System | Sign in");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 600));
        setSize(1080, 720);
        setLocationRelativeTo(null);
        setContentPane(createContent());
        getRootPane().setDefaultButton(signIn);
        setVisible(true);
    }

    private JPanel createContent() {
        JPanel split = new JPanel(new BorderLayout());
        split.setBackground(AppTheme.SURFACE_COLOR);
        AuthImagePanel visual = new AuthImagePanel("/assets/healthcare-team.png",
                "Care teams, working together.", "A desktop workspace for the people and processes that keep hospital operations moving.");
        visual.setPreferredSize(new Dimension(450, 700));
        split.add(visual, BorderLayout.WEST);

        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(AppTheme.SURFACE_COLOR);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setPreferredSize(new Dimension(410, 500));
        GridBagConstraints c = constraints();
        addLabel(form, c, "HOSPITAL MANAGEMENT SYSTEM", true, AppTheme.SECONDARY_COLOR);
        addLabel(form, c, "Welcome back", false, AppTheme.TEXT_PRIMARY);
        addLabel(form, c, "Sign in with your approved staff account.", true, AppTheme.TEXT_SECONDARY);
        addField(form, c, "Username or email", identifier);
        addField(form, c, "Password", password);

        c.gridy++;
        c.insets = new Insets(0, 0, 18, 0);
        JPanel options = new JPanel(new BorderLayout());
        options.setOpaque(false);
        JCheckBox show = new JCheckBox("Show password");
        show.setOpaque(false);
        show.setFont(AppTheme.FONT_SMALL);
        show.setForeground(AppTheme.TEXT_SECONDARY);
        show.addActionListener(event -> password.setEchoChar(show.isSelected() ? '\0' : defaultEchoChar()));
        options.add(show, BorderLayout.WEST);
        JButton forgot = link("Forgot password?");
        forgot.addActionListener(event -> JOptionPane.showMessageDialog(this,
                "Ask your hospital administrator to reset your password.", "Password help", JOptionPane.INFORMATION_MESSAGE));
        options.add(forgot, BorderLayout.EAST);
        form.add(options, c);

        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);
        signIn.setPreferredSize(new Dimension(0, 46));
        signIn.addActionListener(event -> signIn());
        form.add(signIn, c);
        c.gridy++;
        status.setFont(AppTheme.FONT_SMALL);
        status.setForeground(AppTheme.ERROR_COLOR);
        form.add(status, c);
        c.gridy++;
        c.insets = new Insets(12, 0, 0, 0);
        JPanel registration = new JPanel(new BorderLayout(4, 0));
        registration.setOpaque(false);
        JLabel prompt = new JLabel("Need an account?");
        prompt.setFont(AppTheme.FONT_BODY);
        prompt.setForeground(AppTheme.TEXT_SECONDARY);
        JButton register = link("Request access");
        register.addActionListener(event -> { dispose(); new RegistrationFrame(); });
        registration.add(prompt, BorderLayout.WEST);
        registration.add(register, BorderLayout.CENTER);
        form.add(registration, c);
        c.gridy++;
        c.insets = new Insets(18, 0, 0, 0);
        JLabel notice = new JLabel("For authorized hospital staff access.");
        notice.setFont(AppTheme.FONT_SMALL);
        notice.setForeground(AppTheme.TEXT_SECONDARY);
        form.add(notice, c);
        area.add(form);
        split.add(area, BorderLayout.CENTER);
        return split;
    }

    private GridBagConstraints constraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST; c.insets = new Insets(0, 0, 9, 0);
        return c;
    }

    private void addLabel(JPanel panel, GridBagConstraints c, String text, boolean small, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(small ? AppTheme.FONT_BODY : AppTheme.FONT_TITLE);
        label.setForeground(color);
        c.gridy++;
        c.insets = small ? new Insets(0, 0, 8, 0) : new Insets(5, 0, 8, 0);
        panel.add(label, c);
    }

    private void addField(JPanel panel, GridBagConstraints c, String name, JTextField field) {
        c.gridy++;
        c.insets = new Insets(10, 0, 6, 0);
        JLabel label = new JLabel(name);
        label.setFont(AppTheme.FONT_BODY.deriveFont(java.awt.Font.BOLD));
        label.setForeground(AppTheme.TEXT_PRIMARY);
        panel.add(label, c);
        c.gridy++;
        c.insets = new Insets(0, 0, 12, 0);
        field.setFont(AppTheme.FONT_BODY);
        field.setPreferredSize(new Dimension(0, 44));
        panel.add(field, c);
    }

    private JButton link(String label) {
        JButton button = new JButton(label);
        button.setFont(AppTheme.FONT_BODY.deriveFont(java.awt.Font.BOLD));
        button.setForeground(AppTheme.SECONDARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        return button;
    }

    private char defaultEchoChar() {
        Object value = javax.swing.UIManager.get("PasswordField.echoChar");
        return value instanceof Character character ? character : '\u2022';
    }

    private void signIn() {
        char[] credentials = password.getPassword();
        String account = identifier.getText().trim();
        signIn.setEnabled(false);
        status.setForeground(AppTheme.TEXT_SECONDARY);
        status.setText("Signing in…");
        new SwingWorker<AuthenticatedUser, Void>() {
            private String error;
            @Override protected AuthenticatedUser doInBackground() {
                try { return authService.authenticate(account, credentials); }
                catch (AuthService.AuthException exception) { error = exception.getMessage(); return null; }
            }
            @Override protected void done() {
                signIn.setEnabled(true);
                try {
                    AuthenticatedUser user = get();
                    if (user == null) {
                        status.setForeground(AppTheme.ERROR_COLOR);
                        status.setText("<html>" + error.replace("\n", "<br>") + "</html>");
                        password.setText("");
                        return;
                    }
                    dispose();
                    new AdminDashboard(user);
                } catch (Exception exception) {
                    status.setForeground(AppTheme.ERROR_COLOR);
                    status.setText("Unable to sign in. Please try again.");
                }
            }
        }.execute();
    }
}
