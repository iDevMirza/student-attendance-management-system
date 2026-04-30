package org.cardiffmet.utils;

import org.cardiffmet.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class LogoutHelper {
    public static void logout(JFrame currentFrame) {
        int confirm = JOptionPane.showConfirmDialog(
                currentFrame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            currentFrame.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // Creates a styled logout button
    public static JButton createLogoutButton(JFrame frame) {
        JButton btn = new JButton("Logout");
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(178, 34, 52));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> logout(frame));
        return btn;
    }
}
