package org.cardiffmet.observer;

import javax.swing.*;

public class ParentNotifier implements AttendanceObserver {
    @Override
    public void update(String studentId, String studentName, String parentEmail,
                       String date, String status) {
        String message = "Email sent to: " + parentEmail + "\n\n" +
                "Dear Parent,\n" +
                "Your child " + studentName + " (ID: " + studentId + ")\n" +
                "Attendance on " + date + ": " + status + "\n\n" +
                "Regards,\nSchool Admin";
        System.out.println("[NOTIFICATION] " + message);
        JOptionPane.showMessageDialog(null, message, "Parent Notification",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
