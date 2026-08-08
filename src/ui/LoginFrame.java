package ui;

import dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {

        setTitle("Hospital Management System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        createUI();

        setVisible(true);
    }

    private void createUI() {

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("🏥 Hospital Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(70, 30, 320, 30);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(60, 100, 100, 25);

        usernameField = new JTextField();
        usernameField.setBounds(160, 100, 220, 30);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(60, 150, 100, 25);

        passwordField = new JPasswordField();
        passwordField.setBounds(160, 150, 220, 30);

        loginButton = new JButton("LOGIN");
        loginButton.setBounds(160, 210, 120, 35);

        loginButton.addActionListener(e -> login());

        panel.add(titleLabel);
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);

        add(panel);
    }

    private void login() {

        String username = usernameField.getText().trim();

        String password =
                new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Login Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        UserDAO userDAO = new UserDAO();

        String role = userDAO.login(username, password);

        if (role != null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Login successful!\nRole: " + role,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

            if (role.equals("ADMIN")) {
                new AdminDashboard();
            }

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}