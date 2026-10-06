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
import java.awt.*;
import java.util.Arrays;

/** Welcome, portal selection, and credential sign-in for hospital staff and patients. */
public class LoginFrame extends JFrame {
    private static final String[] ROLES = {"ADMIN", "RECEPTIONIST", "DOCTOR", "ACCOUNTANT"};
    private static final String[] DESCRIPTIONS = {"Administrator access", "Front desk and patient operations",
            "Clinical and appointment workspace", "Billing and financial workspace"};
    private String selectedRole;
    private JPanel content;
    private final JTextField identifier = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final PrimaryButton signIn = new PrimaryButton("Sign in");
    private final JLabel status = new JLabel(" ");
    private final AuthService staffAuth = new AuthService();
    private final PatientAuthService patientAuth = new PatientAuthService();

    public LoginFrame() {
        setTitle("Hospital Management System | Sign in"); setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(720, 560)); setSize(980, 700); setLocationRelativeTo(null);
        showWelcome(); setVisible(true);
    }

    private void showWelcome() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND_COLOR);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(26, 42, 16, 42));
        JLabel brand = new JLabel("✚   Hospital Management System");
        brand.setFont(AppTheme.FONT_HEADING);
        brand.setForeground(AppTheme.PRIMARY_COLOR);
        JLabel subtitle = new JLabel("Secure Healthcare Management");
        subtitle.setFont(AppTheme.FONT_SMALL);
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);
        JPanel brandBlock = new JPanel();
        brandBlock.setOpaque(false);
        brandBlock.setLayout(new BoxLayout(brandBlock, BoxLayout.Y_AXIS));
        brandBlock.add(brand);
        brandBlock.add(Box.createVerticalStrut(5));
        brandBlock.add(subtitle);
        header.add(brandBlock, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(760, 450));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(0, 12, 10, 12);
        JLabel title = new JLabel("Welcome to Hospital Management System");
        title.setFont(AppTheme.FONT_TITLE.deriveFont(Font.BOLD, 25f));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        content.add(title, c);
        c.gridy++;
        JLabel support = new JLabel("<html>Secure access to your healthcare workspace.<br>Choose how you would like to sign in to continue.</html>");
        support.setFont(AppTheme.FONT_BODY);
        support.setForeground(AppTheme.TEXT_SECONDARY);
        content.add(support, c);

        c.gridy++; c.gridwidth = 1; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(28, 12, 12, 12);
        content.add(portalCard("✚", "STAFF PORTAL", "Admin, Receptionist, Doctor, Accountant", this::showRoles), c);
        c.gridx = 1;
        content.add(portalCard("♡", "PATIENT PORTAL", "Appointments and hospital information", () -> {
            selectedRole = "PATIENT";
            showLogin();
        }), c);

        c.gridx = 0; c.gridy++; c.gridwidth = 2; c.weighty = 0; c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER; c.insets = new Insets(14, 12, 0, 12);
        JButton exit = link("Exit");
        exit.setForeground(AppTheme.TEXT_SECONDARY);
        exit.addActionListener(e -> dispose());
        content.add(exit, c);
        center.add(content);
        root.add(center, BorderLayout.CENTER);

        JLabel footer = new JLabel("Authorized access only  •  Your session is protected", SwingConstants.CENTER);
        footer.setFont(AppTheme.FONT_SMALL);
        footer.setForeground(AppTheme.TEXT_SECONDARY);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
        getRootPane().setDefaultButton(null);
        revalidate(); repaint();
    }

    private JButton portalCard(String icon, String title, String description, Runnable action) {
        JButton card = new JButton();
        card.setLayout(new BorderLayout(14, 8));
        card.setHorizontalAlignment(SwingConstants.LEFT);
        card.setBackground(Color.WHITE);
        card.setOpaque(true);
        card.setFocusPainted(true);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)));
        JLabel glyph = new JLabel(icon, SwingConstants.CENTER);
        glyph.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        glyph.setForeground(AppTheme.SECONDARY_COLOR);
        glyph.setPreferredSize(new Dimension(54, 54));
        glyph.setOpaque(true);
        glyph.setBackground(new Color(232, 246, 247));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel(title);
        heading.setFont(AppTheme.FONT_SUBHEADING);
        heading.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel detail = new JLabel("<html><div style='width:205px'>" + description + "</div></html>");
        detail.setFont(AppTheme.FONT_BODY);
        detail.setForeground(AppTheme.TEXT_SECONDARY);
        detail.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        text.add(heading);
        text.add(detail);
        JLabel arrow = new JLabel("→");
        arrow.setFont(AppTheme.FONT_HEADING);
        arrow.setForeground(AppTheme.SECONDARY_COLOR);
        card.add(glyph, BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);
        card.addActionListener(e -> action.run());
        card.addChangeListener(e -> {
            Color line = card.getModel().isPressed() ? AppTheme.PRIMARY_COLOR
                    : card.getModel().isRollover() || card.hasFocus() ? AppTheme.SECONDARY_COLOR : AppTheme.BORDER_COLOR;
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(line,
                    card.getModel().isRollover() || card.hasFocus() ? 2 : 1),
                    BorderFactory.createEmptyBorder(21, 21, 21, 21)));
        });
        return card;
    }

    private void showRoles() {
        selectedRole = null;
        JPanel area = new JPanel(new GridBagLayout()); area.setBackground(AppTheme.BACKGROUND_COLOR);
        JPanel panel = new JPanel(new GridBagLayout()); panel.setOpaque(false); panel.setPreferredSize(new Dimension(880, 620));
        GridBagConstraints c = new GridBagConstraints(); c.gridx=0; c.gridy=0; c.gridwidth=2; c.fill=GridBagConstraints.HORIZONTAL; c.weightx=1; c.insets=new Insets(6,8,16,8);
        JLabel brand = new JLabel("HOSPITAL MANAGEMENT SYSTEM"); brand.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD)); brand.setForeground(AppTheme.SECONDARY_COLOR); panel.add(brand,c);
        c.gridy++; JLabel title = new JLabel("How would you like to sign in?"); title.setFont(AppTheme.FONT_TITLE); title.setForeground(AppTheme.TEXT_PRIMARY); panel.add(title,c);
        c.gridy++; JLabel sub = new JLabel("Choose the workspace that matches your account."); sub.setFont(AppTheme.FONT_BODY); sub.setForeground(AppTheme.TEXT_SECONDARY); panel.add(sub,c);
        c.gridy++; c.gridwidth=1; c.insets=new Insets(10,8,10,8); c.fill=GridBagConstraints.BOTH; c.weighty=1;
        ButtonGroup group = new ButtonGroup();
        for(int i=0;i<ROLES.length;i++) { final int index=i; JToggleButton card=new JToggleButton("<html><div style='width:245px;padding:10px'><b>"+ROLES[i]+"</b><br><br><span style='font-size:small'>"+DESCRIPTIONS[i]+"</span></div></html>");
            card.setHorizontalAlignment(SwingConstants.LEFT); card.setFont(AppTheme.FONT_BODY); card.setForeground(AppTheme.TEXT_PRIMARY); card.setBackground(Color.WHITE); card.setFocusPainted(true); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR,1),BorderFactory.createEmptyBorder(9,12,9,12))); card.setPreferredSize(new Dimension(275,120));
            card.addChangeListener(e->{card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(card.isSelected()?AppTheme.ACCENT_COLOR:(card.getModel().isRollover()?AppTheme.SECONDARY_COLOR:AppTheme.BORDER_COLOR),card.isSelected()?2:1),BorderFactory.createEmptyBorder(9,12,9,12)));});
            card.addActionListener(e->selectedRole=ROLES[index]); group.add(card); c.gridx=i%2; if(i==2)c.gridy++; panel.add(card,c);
        }
        c.gridy++; c.gridx=0;c.gridwidth=2;c.weighty=0;c.fill=GridBagConstraints.HORIZONTAL;c.insets=new Insets(10,8,5,8);
        PrimaryButton next=new PrimaryButton("Continue"); next.addActionListener(e->{if(selectedRole!=null)showLogin();}); panel.add(next,c);
        c.gridy++; c.insets=new Insets(4,8,0,8);
        JButton back=link("← Back to welcome"); back.addActionListener(e->showWelcome()); panel.add(back,c);
        area.add(panel); content=area; setContentPane(area); getRootPane().setDefaultButton(next); revalidate(); repaint();
    }

    private void showLogin() {
        JPanel split=new JPanel(new BorderLayout()); split.setBackground(AppTheme.SURFACE_COLOR);
        AuthImagePanel visual=new AuthImagePanel("/assets/healthcare-team.png","Care teams, working together.","A secure workspace for hospital operations and patient information."); visual.setPreferredSize(new Dimension(430,700)); split.add(visual,BorderLayout.WEST);
        JPanel area=new JPanel(new GridBagLayout()); area.setBackground(AppTheme.SURFACE_COLOR);
        JPanel form=new JPanel(new GridBagLayout()); form.setOpaque(false); form.setPreferredSize(new Dimension(430,520)); GridBagConstraints c=new GridBagConstraints(); c.gridx=0;c.gridy=0;c.weightx=1;c.fill=GridBagConstraints.HORIZONTAL;c.anchor=GridBagConstraints.WEST;c.insets=new Insets(0,0,10,0);
        JLabel brand=new JLabel("HOSPITAL MANAGEMENT SYSTEM");brand.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));brand.setForeground(AppTheme.SECONDARY_COLOR);form.add(brand,c);
        c.gridy++; JLabel heading=new JLabel(selectedRole+" LOGIN");heading.setFont(AppTheme.FONT_TITLE);heading.setForeground(AppTheme.TEXT_PRIMARY);form.add(heading,c);
        c.gridy++; JLabel description=new JLabel(DESCRIPTIONS[Arrays.asList(ROLES).indexOf(selectedRole)]);description.setFont(AppTheme.FONT_BODY);description.setForeground(AppTheme.TEXT_SECONDARY);form.add(description,c);
        c.gridy++; c.insets=new Insets(22,0,6,0); JLabel idLabel=new JLabel(selectedRole.equals("PATIENT")?"Email or patient username":"Username or email");idLabel.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));form.add(idLabel,c);
        c.gridy++;c.insets=new Insets(0,0,12,0);identifier.setText("");identifier.setFont(AppTheme.FONT_BODY);identifier.setPreferredSize(new Dimension(0,44));form.add(identifier,c);
        c.gridy++;c.insets=new Insets(8,0,6,0);JLabel passLabel=new JLabel("Password");passLabel.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));form.add(passLabel,c);
        c.gridy++;c.insets=new Insets(0,0,10,0);password.setText("");password.setFont(AppTheme.FONT_BODY);password.setPreferredSize(new Dimension(0,44));form.add(password,c);
        c.gridy++;c.insets=new Insets(0,0,18,0);JPanel opts=new JPanel(new BorderLayout());opts.setOpaque(false);JCheckBox show=new JCheckBox("Show password");show.setOpaque(false);show.setFont(AppTheme.FONT_SMALL);show.addActionListener(e->password.setEchoChar(show.isSelected()? '\0':defaultEchoChar()));opts.add(show,BorderLayout.WEST);
        if(!selectedRole.equals("PATIENT")){JButton forgot=link("Forgot password?");forgot.addActionListener(e->JOptionPane.showMessageDialog(this,"Ask your hospital administrator to reset your password.","Password help",JOptionPane.INFORMATION_MESSAGE));opts.add(forgot,BorderLayout.EAST);} form.add(opts,c);
        c.gridy++;c.insets=new Insets(0,0,8,0);signIn.setPreferredSize(new Dimension(0,46));signIn.addActionListener(e->signIn());form.add(signIn,c);
        c.gridy++;status.setFont(AppTheme.FONT_SMALL);status.setForeground(AppTheme.ERROR_COLOR);form.add(status,c);
        if(!selectedRole.equals("PATIENT")){c.gridy++;c.insets=new Insets(10,0,0,0);JPanel reg=new JPanel(new BorderLayout());reg.setOpaque(false);JLabel prompt=new JLabel("Need an account?");prompt.setForeground(AppTheme.TEXT_SECONDARY);JButton request=link("Request access");request.addActionListener(e->{dispose();new RegistrationFrame();});reg.add(prompt,BorderLayout.WEST);reg.add(request,BorderLayout.CENTER);form.add(reg,c);}
        c.gridy++;c.insets=new Insets(18,0,0,0);JButton change=link("← Back");change.addActionListener(e->{if("PATIENT".equals(selectedRole))showWelcome();else showRoles();});form.add(change,c);
        area.add(form);split.add(area,BorderLayout.CENTER);content=split;setContentPane(split);getRootPane().setDefaultButton(signIn);revalidate();repaint();identifier.requestFocusInWindow();
    }
    private JButton link(String text){JButton b=new JButton(text);b.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));b.setForeground(AppTheme.SECONDARY_COLOR);b.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));b.setContentAreaFilled(false);b.setFocusPainted(true);return b;}
    private char defaultEchoChar(){Object value=UIManager.get("PasswordField.echoChar");return value instanceof Character ch?ch:'\u2022';}
    private void signIn(){char[] secret=password.getPassword();String account=identifier.getText().trim();signIn.setEnabled(false);status.setForeground(AppTheme.TEXT_SECONDARY);status.setText("Signing in…");
        if("PATIENT".equals(selectedRole)){new SwingWorker<PatientIdentity,Void>(){private String error;protected PatientIdentity doInBackground(){try{return patientAuth.authenticate(account,secret);}catch(PatientAuthService.AuthException ex){error=ex.getMessage();return null;}}protected void done(){signIn.setEnabled(true);try{PatientIdentity patient=get();if(patient==null){showError(error);password.setText("");return;}dispose();new PatientDashboard(patient);}catch(Exception ex){showError("Unable to sign in. Please try again.");}}}.execute();return;}
        UserRole requested=UserRole.valueOf(selectedRole);new SwingWorker<AuthenticatedUser,Void>(){private String error;protected AuthenticatedUser doInBackground(){try{AuthenticatedUser user=staffAuth.authenticate(account,secret);if(user.role()!=requested){staffAuth.logout();error="This account does not have access to the selected login type.";return null;}return user;}catch(AuthService.AuthException ex){error=ex.getMessage();return null;}}protected void done(){signIn.setEnabled(true);try{AuthenticatedUser user=get();if(user==null){showError(error);password.setText("");return;}dispose();new AdminDashboard(user);}catch(Exception ex){showError("Unable to sign in. Please try again.");}}}.execute();
    }
    private void showError(String error){status.setForeground(AppTheme.ERROR_COLOR);status.setText("<html>"+(error==null?"Unable to sign in. Please try again.":error.replace("\n","<br>"))+"</html>");}
}
