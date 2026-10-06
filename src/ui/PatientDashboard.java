package ui;

import model.PatientIdentity;
import security.PatientSession;
import service.PatientAuthService;
import ui.theme.AppTheme;
import javax.swing.*;
import java.awt.*;

/** Patient-facing landing page; does not construct or expose staff modules. */
public final class PatientDashboard extends JFrame {
    public PatientDashboard(PatientIdentity patient){
        super("Hospital Management System | Patient");
        PatientIdentity active=PatientSession.INSTANCE.current().orElseThrow(()->new IllegalStateException("Patient sign-in is required."));
        if(active.patientId()!=patient.patientId())throw new IllegalStateException("Patient session does not match.");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);setSize(900,620);setMinimumSize(new Dimension(760,520));setLocationRelativeTo(null);
        JPanel root=new JPanel(new BorderLayout());root.setBackground(AppTheme.BACKGROUND_COLOR);
        JLabel header=new JLabel("  Hospital Management System     |     "+active.fullName()+"  ");header.setOpaque(true);header.setBackground(AppTheme.PRIMARY_COLOR);header.setForeground(Color.WHITE);header.setFont(AppTheme.FONT_HEADING);header.setBorder(BorderFactory.createEmptyBorder(20,12,20,12));root.add(header,BorderLayout.NORTH);
        JPanel welcome=new JPanel();welcome.setBackground(AppTheme.BACKGROUND_COLOR);welcome.setLayout(new BoxLayout(welcome,BoxLayout.Y_AXIS));welcome.setBorder(AppTheme.pagePadding());JLabel title=new JLabel("Welcome");title.setFont(AppTheme.FONT_TITLE);title.setForeground(AppTheme.TEXT_PRIMARY);JLabel text=new JLabel("Your appointments and hospital information will appear here.");text.setFont(AppTheme.FONT_BODY);text.setForeground(AppTheme.TEXT_SECONDARY);welcome.add(title);welcome.add(Box.createVerticalStrut(12));welcome.add(text);root.add(welcome,BorderLayout.CENTER);
        JButton logout=new JButton("Sign out");logout.addActionListener(e->{new PatientAuthService().logout();dispose();SwingUtilities.invokeLater(LoginFrame::new);});JPanel south=new JPanel(new FlowLayout(FlowLayout.RIGHT));south.setBackground(AppTheme.BACKGROUND_COLOR);south.add(logout);root.add(south,BorderLayout.SOUTH);setContentPane(root);setVisible(true);
    }
}
