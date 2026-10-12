package ui;

import model.AuthenticatedUser;
import model.PatientIdentity;
import model.UserRole;
import service.AuthService;
import service.PatientAuthService;
import ui.components.AuthImagePanel;
import ui.components.PrimaryButton;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Arrays;

/** Welcome, portal selection, and credential sign-in for hospital staff and patients. */
public class LoginFrame extends JFrame {
    private static final String[] ROLES = {"ADMIN", "RECEPTIONIST", "DOCTOR", "ACCOUNTANT"};
    private static final String[] DESCRIPTIONS = {"Administrator access", "Front desk and patient operations",
            "Clinical and appointment workspace", "Billing and financial workspace"};
    private String selectedRole;
    private final JTextField identifier = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final PrimaryButton signIn = new PrimaryButton("Sign in");
    private final JLabel status = new JLabel(" ");
    private final AuthService staffAuth = new AuthService();
    private final PatientAuthService patientAuth = new PatientAuthService();

    public LoginFrame() {
        setTitle("Hospital Management System | Sign in"); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(720, 560)); setSize(980, 700); setLocationRelativeTo(null);
        signIn.addActionListener(e -> signIn());
        showWelcome(); setVisible(true);
    }

    private void showWelcome() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 10, 8, 10);
        JLabel title = new JLabel("Welcome to Hospital Management System");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 28f));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        content.add(title, c);
        c.gridy++;
        JLabel support = new JLabel(
                "<html><div style='text-align:center'>Secure access to your healthcare workspace. "
                        + "Choose how you would like to sign in.</div></html>");
        support.setHorizontalAlignment(SwingConstants.CENTER);
        support.setFont(AppTheme.FONT_BODY);
        support.setForeground(AppTheme.TEXT_SECONDARY);
        content.add(support, c);

        c.gridy++;
        c.gridwidth = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(30, 10, 18, 10);
        content.add(portalCard("✚", "Staff Portal", "Secure tools for hospital staff", this::showRoles), c);
        c.gridx = 1;
        content.add(portalCard("♡", "Patient Portal", "Access your patient workspace", () -> {
            selectedRole = "PATIENT";
            showLogin();
        }), c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 10, 0, 10);
        JButton exit = link("Exit application");
        exit.setForeground(AppTheme.TEXT_SECONDARY);
        exit.addActionListener(e -> dispose());
        content.add(exit, c);

        setScreen(content);
        getRootPane().setDefaultButton(null);
    }

    private JButton portalCard(String icon, String title, String description, Runnable action) {
        JButton card = new JButton();
        card.setLayout(new BorderLayout(16, 8));
        card.setHorizontalAlignment(SwingConstants.LEFT);
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setFocusPainted(true);
        card.setFont(AppTheme.FONT_BODY);
        card.getAccessibleContext().setAccessibleName(title + " portal");
        card.getAccessibleContext().setAccessibleDescription(description);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(cardBorder(AppTheme.BORDER_COLOR, 1, 24, 22));
        JLabel glyph = new JLabel(icon, SwingConstants.CENTER);
        glyph.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        glyph.setForeground(AppTheme.SECONDARY_COLOR);
        glyph.setPreferredSize(new Dimension(58, 58));
        glyph.setOpaque(true);
        glyph.setBackground(new Color(230, 243, 247));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(AppTheme.FONT_HEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel detail = new JLabel("<html><div style='width:210px'>" + description + "</div></html>");
        detail.setFont(AppTheme.FONT_BODY);
        detail.setForeground(AppTheme.TEXT_SECONDARY);
        detail.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        text.add(heading);
        text.add(detail);
        JLabel arrow = new JLabel("→");
        arrow.setFont(AppTheme.FONT_HEADING);
        arrow.setForeground(AppTheme.SECONDARY_COLOR);
        arrow.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        card.add(glyph, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);
        card.addActionListener(e -> action.run());
        card.addChangeListener(e -> {
            boolean emphasized = card.getModel().isRollover() || card.hasFocus() || card.getModel().isPressed();
            card.setBorder(cardBorder(card.getModel().isPressed() ? AppTheme.PRIMARY_COLOR
                    : emphasized ? AppTheme.SECONDARY_COLOR : AppTheme.BORDER_COLOR, emphasized ? 2 : 1, 23, 21));
        });
        return card;
    }

    private void showRoles() {
        selectedRole = null;
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 42, 24, 42));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 8, 8, 8);
        JLabel title = new JLabel("Choose your staff workspace");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        panel.add(title, c);
        c.gridy++;
        JLabel sub = new JLabel("Select the role associated with your account to continue.");
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);
        c.insets = new Insets(0, 8, 18, 8);
        panel.add(sub, c);

        c.gridy++;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(8, 8, 8, 8);
        ButtonGroup group = new ButtonGroup();
        PrimaryButton next = new PrimaryButton("Continue");
        next.setEnabled(false);
        next.setPreferredSize(new Dimension(0, 46));
        for (int i = 0; i < ROLES.length; i++) {
            final int index = i;
            JToggleButton card = roleCard(ROLES[i]);
            card.addActionListener(e -> {
                selectedRole = ROLES[index];
                next.setEnabled(true);
            });
            group.add(card);
            c.gridx = i % 2;
            if (i == 2) c.gridy++;
            panel.add(card, c);
        }

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(12, 8, 4, 8);
        next.addActionListener(e -> {
            if (selectedRole != null) showLogin();
        });
        panel.add(next, c);
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(4, 8, 0, 8);
        JButton back = link("← Back to welcome");
        back.addActionListener(e -> showWelcome());
        panel.add(back, c);
        setScreen(panel);
        getRootPane().setDefaultButton(next);
    }

    private JToggleButton roleCard(String role) {
        JToggleButton card = new JToggleButton();
        card.setLayout(new BorderLayout(14, 4));
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setFocusPainted(true);
        card.setFocusable(true);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(cardBorder(AppTheme.BORDER_COLOR, 1, 18, 16));
        card.setPreferredSize(new Dimension(280, 128));
        card.getAccessibleContext().setAccessibleName(role + " login");
        card.getAccessibleContext().setAccessibleDescription(descriptionForRole(role));

        JLabel icon = new JLabel(roleIcon(role), SwingConstants.CENTER);
        icon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 25));
        icon.setForeground(AppTheme.SECONDARY_COLOR);
        icon.setBackground(new Color(230, 243, 247));
        icon.setOpaque(true);
        icon.setPreferredSize(new Dimension(48, 48));

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(role);
        heading.setFont(AppTheme.FONT_SUBHEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel description = new JLabel("<html><div style='width:170px'>" + descriptionForRole(role) + "</div></html>");
        description.setFont(AppTheme.FONT_SMALL);
        description.setForeground(AppTheme.TEXT_SECONDARY);
        description.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        labels.add(heading);
        labels.add(description);
        JLabel selected = new JLabel("›");
        selected.setFont(AppTheme.FONT_HEADING);
        selected.setForeground(AppTheme.ACCENT_COLOR);
        card.add(icon, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
        card.add(selected, BorderLayout.EAST);
        card.addChangeListener(event -> updateRoleCard(card));
        return card;
    }

    private void updateRoleCard(JToggleButton card) {
        boolean selected = card.isSelected();
        boolean emphasized = selected || card.getModel().isRollover() || card.hasFocus();
        JLabel indicator = (JLabel) ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.EAST);
        indicator.setText(selected ? "✓" : "›");
        card.setBackground(selected ? new Color(239, 248, 249) : Color.WHITE);
        card.setBorder(cardBorder(selected ? AppTheme.ACCENT_COLOR
                : emphasized ? AppTheme.SECONDARY_COLOR : AppTheme.BORDER_COLOR,
                selected || card.hasFocus() ? 2 : 1, selected ? 17 : 18, selected ? 15 : 16));
    }

    private Border cardBorder(Color color, int thickness, int verticalPadding, int horizontalPadding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness),
                BorderFactory.createEmptyBorder(verticalPadding, horizontalPadding,
                        verticalPadding, horizontalPadding));
    }

    private void setScreen(JPanel body) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND_COLOR);

        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setOpaque(true);
        header.setBackground(AppTheme.SURFACE_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 34, 14, 34)));
        JLabel mark = new JLabel("✚", SwingConstants.CENTER);
        mark.setOpaque(true);
        mark.setBackground(AppTheme.PRIMARY_COLOR);
        mark.setForeground(Color.WHITE);
        mark.setFont(AppTheme.FONT_SUBHEADING);
        mark.setPreferredSize(new Dimension(40, 40));
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Hospital Management System");
        name.setFont(AppTheme.FONT_SUBHEADING);
        name.setForeground(AppTheme.PRIMARY_COLOR);
        JLabel subtitle = new JLabel("Secure Healthcare Management");
        subtitle.setFont(AppTheme.FONT_SMALL);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        brand.add(name);
        brand.add(Box.createVerticalStrut(3));
        brand.add(subtitle);
        header.add(mark, BorderLayout.WEST);
        header.add(brand, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.add(body);
        JLabel footer = new JLabel("Authorized access only  •  Your session is protected",
                SwingConstants.CENTER);
        footer.setFont(AppTheme.FONT_SMALL);
        footer.setForeground(AppTheme.TEXT_SECONDARY);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 12, 16, 12));
        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
        revalidate();
        repaint();
    }
    private String roleIcon(String role) {
        return switch (role) {
            case "ADMIN" -> "⚙";
            case "RECEPTIONIST" -> "☎";
            case "DOCTOR" -> "✚";
            case "ACCOUNTANT" -> "$";
            default -> throw new IllegalArgumentException("Unknown staff role.");
        };
    }

    private void showLogin() {
        JPanel split=new JPanel(new BorderLayout()); split.setBackground(AppTheme.SURFACE_COLOR);
        AuthImagePanel visual=new AuthImagePanel("/assets/healthcare-team.png","Care teams, working together.","A secure workspace for hospital operations and patient information."); visual.setPreferredSize(new Dimension(430,700)); split.add(visual,BorderLayout.WEST);
        JPanel area=new JPanel(new GridBagLayout()); area.setBackground(AppTheme.BACKGROUND_COLOR);
        JPanel form=new JPanel(new GridBagLayout()); form.setBackground(AppTheme.SURFACE_COLOR); form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),BorderFactory.createEmptyBorder(30,34,30,34))); form.setPreferredSize(new Dimension(460,540)); GridBagConstraints c=new GridBagConstraints(); c.gridx=0;c.gridy=0;c.weightx=1;c.fill=GridBagConstraints.HORIZONTAL;c.anchor=GridBagConstraints.WEST;c.insets=new Insets(0,0,10,0);
        JLabel brand=new JLabel("HOSPITAL MANAGEMENT SYSTEM");brand.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));brand.setForeground(AppTheme.SECONDARY_COLOR);form.add(brand,c);
        c.gridy++; JLabel heading=new JLabel(selectedRole+" LOGIN");heading.setFont(AppTheme.FONT_TITLE);heading.setForeground(AppTheme.TEXT_PRIMARY);form.add(heading,c);
        c.gridy++; JLabel description=new JLabel(descriptionForRole(selectedRole));description.setFont(AppTheme.FONT_BODY);description.setForeground(AppTheme.TEXT_SECONDARY);form.add(description,c);
        c.gridy++; c.insets=new Insets(22,0,6,0); JLabel idLabel=new JLabel(selectedRole.equals("PATIENT")?"Email or patient username":"Username or email");idLabel.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));idLabel.setLabelFor(identifier);form.add(idLabel,c);
        c.gridy++;c.insets=new Insets(0,0,12,0);identifier.setText("");identifier.putClientProperty("JTextField.placeholderText", selectedRole.equals("PATIENT") ? "Enter your patient username or email" : "Enter your staff username or email");identifier.setFont(AppTheme.FONT_BODY);identifier.setPreferredSize(new Dimension(0,44));identifier.getAccessibleContext().setAccessibleName(idLabel.getText());form.add(identifier,c);
        c.gridy++;c.insets=new Insets(8,0,6,0);JLabel passLabel=new JLabel("Password");passLabel.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));passLabel.setLabelFor(password);form.add(passLabel,c);
        c.gridy++;c.insets=new Insets(0,0,10,0);password.setText("");password.setFont(AppTheme.FONT_BODY);password.setPreferredSize(new Dimension(0,44));password.getAccessibleContext().setAccessibleName("Password");form.add(password,c);
        c.gridy++;c.insets=new Insets(0,0,18,0);JPanel opts=new JPanel(new BorderLayout());opts.setOpaque(false);JCheckBox show=new JCheckBox("Show password");show.setOpaque(false);show.setFont(AppTheme.FONT_SMALL);show.addActionListener(e->password.setEchoChar(show.isSelected()? '\0':defaultEchoChar()));opts.add(show,BorderLayout.WEST);
        if(!selectedRole.equals("PATIENT")){JButton forgot=link("Forgot password?");forgot.addActionListener(e->JOptionPane.showMessageDialog(this,"Ask your hospital administrator to reset your password.","Password help",JOptionPane.INFORMATION_MESSAGE));opts.add(forgot,BorderLayout.EAST);} form.add(opts,c);
        c.gridy++;c.insets=new Insets(0,0,8,0);signIn.setPreferredSize(new Dimension(0,46));form.add(signIn,c);
        c.gridy++;status.setFont(AppTheme.FONT_SMALL);status.setForeground(AppTheme.ERROR_COLOR);form.add(status,c);
        if(!selectedRole.equals("PATIENT")){c.gridy++;c.insets=new Insets(10,0,0,0);JPanel reg=new JPanel(new BorderLayout());reg.setOpaque(false);JLabel prompt=new JLabel("Need an account?");prompt.setForeground(AppTheme.TEXT_SECONDARY);JButton request=link("Request access");request.addActionListener(e->{dispose();new RegistrationFrame();});reg.add(prompt,BorderLayout.WEST);reg.add(request,BorderLayout.CENTER);form.add(reg,c);}
        c.gridy++;c.insets=new Insets(18,0,0,0);JButton change=link("← Back");change.addActionListener(e->{if("PATIENT".equals(selectedRole))showWelcome();else showRoles();});form.add(change,c);
        area.add(form);split.add(area,BorderLayout.CENTER);setContentPane(split);getRootPane().setDefaultButton(signIn);status.setText(" ");revalidate();repaint();SwingUtilities.invokeLater(identifier::requestFocusInWindow);
    }
    private JButton link(String text){JButton b=new JButton(text);b.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));b.setForeground(AppTheme.SECONDARY_COLOR);b.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));b.setContentAreaFilled(false);b.setFocusPainted(true);return b;}
    static String descriptionForRole(String role){
        if("PATIENT".equals(role))return "Patient access to your healthcare workspace.";
        int index=Arrays.asList(ROLES).indexOf(role);
        if(index<0)throw new IllegalArgumentException("Unknown login type.");
        return DESCRIPTIONS[index];
    }
    private char defaultEchoChar(){Object value=UIManager.get("PasswordField.echoChar");return value instanceof Character ch?ch:'\u2022';}
    private void signIn(){char[] secret=password.getPassword();String account=identifier.getText().trim();signIn.setEnabled(false);status.setForeground(AppTheme.TEXT_SECONDARY);status.setText("Signing in…");
        if("PATIENT".equals(selectedRole)){new SwingWorker<PatientIdentity,Void>(){private String error;protected PatientIdentity doInBackground(){try{return patientAuth.authenticate(account,secret);}catch(PatientAuthService.AuthException ex){error=ex.getMessage();return null;}}protected void done(){signIn.setEnabled(true);try{PatientIdentity patient=get();if(patient==null){showError(error);password.setText("");return;}dispose();new PatientDashboard(patient);}catch(Exception ex){showError("Unable to sign in. Please try again.");}}}.execute();return;}
        UserRole requested=UserRole.valueOf(selectedRole);new SwingWorker<AuthenticatedUser,Void>(){private String error;protected AuthenticatedUser doInBackground(){try{AuthenticatedUser user=staffAuth.authenticate(account,secret);if(user.role()!=requested){staffAuth.logout();error="This account does not have access to the selected login type.";return null;}return user;}catch(AuthService.AuthException ex){error=ex.getMessage();return null;}}protected void done(){signIn.setEnabled(true);try{AuthenticatedUser user=get();if(user==null){showError(error);password.setText("");return;}dispose();new AdminDashboard(user);}catch(Exception ex){showError("Unable to sign in. Please try again.");}}}.execute();
    }
    private void showError(String error){status.setForeground(AppTheme.ERROR_COLOR);status.setText("<html>"+(error==null?"Unable to sign in. Please try again.":error.replace("\n","<br>"))+"</html>");}
}
