package ui;

import model.PatientIdentity;
import security.PatientSession;
import service.PatientAuthService;
import ui.components.AppHeader;
import ui.components.PrimaryButton;
import ui.theme.AppTheme;
import javax.swing.*;
import java.awt.*;

/** Patient-facing landing page; does not construct or expose staff modules. */
public final class PatientDashboard extends JFrame {
    public PatientDashboard(PatientIdentity patient){
        super("Hospital Management System | Patient");
        PatientIdentity active=PatientSession.INSTANCE.current().orElseThrow(()->new IllegalStateException("Patient sign-in is required."));
        if(active.patientId()!=patient.patientId())throw new IllegalStateException("Patient session does not match.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);setSize(1000,680);setMinimumSize(new Dimension(760,520));setLocationRelativeTo(null);
        JPanel root=new JPanel(new BorderLayout());root.setBackground(AppTheme.BACKGROUND_COLOR);
        root.add(new AppHeader("Patient Portal",active.fullName(),"PATIENT"),BorderLayout.NORTH);
        JPanel welcome=new JPanel(new GridBagLayout());welcome.setBackground(AppTheme.BACKGROUND_COLOR);welcome.setBorder(AppTheme.pagePadding());
        JPanel message=new JPanel();message.setBackground(AppTheme.SURFACE_COLOR);message.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER_COLOR),BorderFactory.createEmptyBorder(32,36,32,36)));message.setLayout(new BoxLayout(message,BoxLayout.Y_AXIS));
        JLabel title=new JLabel("Welcome, "+active.fullName());title.setFont(AppTheme.FONT_TITLE);title.setForeground(AppTheme.TEXT_PRIMARY);
        JLabel text=new JLabel("<html><div style='width:460px'>Patient appointment and records features are not available in this version. You can safely sign out below.</div></html>");text.setFont(AppTheme.FONT_BODY);text.setForeground(AppTheme.TEXT_SECONDARY);
        message.add(title);message.add(Box.createVerticalStrut(AppTheme.SPACE_MD));message.add(text);welcome.add(message);root.add(welcome,BorderLayout.CENTER);
        PrimaryButton logout=new PrimaryButton("Sign out");logout.addActionListener(e->{new PatientAuthService().logout();dispose();SwingUtilities.invokeLater(LoginFrame::new);});
        JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT,AppTheme.SPACE_MD,AppTheme.SPACE_MD));south.setBackground(AppTheme.BACKGROUND_COLOR);south.add(logout);root.add(south,BorderLayout.SOUTH);setContentPane(root);setVisible(true);
    }
}
