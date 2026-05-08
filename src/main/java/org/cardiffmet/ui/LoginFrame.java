package org.cardiffmet.ui;

import org.cardiffmet.exceptions.InvalidUserException;
import org.cardiffmet.facade.SchoolFacade;
import org.cardiffmet.models.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;
    private SchoolFacade facade;

    public LoginFrame() {
        facade = new SchoolFacade();

        setTitle("Smart Attendance Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"HEADTEACHER", "TEACHER", "STUDENT"});
        panel.add(roleBox);

        panel.add(new JLabel("User ID:"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);

        JButton loginBtn = new JButton("Login");
        panel.add(new JLabel());
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> doLogin());
        add(panel);
    }

    private void doLogin() {
        String role = (String) roleBox.getSelectedItem();
        String id = idField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (id.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ID and Password");
            return;
        }

        try {
            User user = facade.authenticate(id, pass, role);
            openDashboard(user);
            dispose();
        } catch (InvalidUserException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case "HEADTEACHER":
                new HeadteacherDashboard(user).setVisible(true);
                break;
            case "TEACHER":
                new TeacherDashboard(user).setVisible(true);
                break;
            case "STUDENT":
                new StudentDashboard(user).setVisible(true);
                break;
        }
    }
}
