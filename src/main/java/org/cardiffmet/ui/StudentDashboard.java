package org.cardiffmet.ui;

import org.cardiffmet.models.Student;
import org.cardiffmet.models.User;
import org.cardiffmet.services.AttendanceService;
import org.cardiffmet.services.AttendanceServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame{
    private User user;
    private AttendanceService service;

    public StudentDashboard(User user) {
        this.user = user;
        this.service = new AttendanceServiceImpl();

        Student student = (Student) user;
        setTitle("Student Dashboard - " + user.getName() + " | Class: " + student.getClassName());
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("My Attendance Records", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(header, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Date", "Class", "Status"}, 0);
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel summary = new JLabel("", SwingConstants.CENTER);
        summary.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(summary, BorderLayout.SOUTH);

        List<String[]> data = service.getAttendanceByStudent(user.getUserId());
        int present = 0, absent = 0;
        for (String[] row : data) {
            model.addRow(row);
            if (row[2].equals("Present")) present++;
            else if (row[2].equals("Absent")) absent++;
        }
        int total = present + absent;
        double pct = total > 0 ? (present * 100.0 / total) : 0;
        summary.setText(String.format("Total: %d | Present: %d | Absent: %d | Percentage: %.2f%%",
                total, present, absent, pct));

        add(p);
    }
}
