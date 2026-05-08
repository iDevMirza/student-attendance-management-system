package org.cardiffmet.ui;

import org.cardiffmet.database.DatabaseConnection;
import org.cardiffmet.exceptions.AttendanceException;
import org.cardiffmet.exceptions.ValidationException;
import org.cardiffmet.models.User;
import org.cardiffmet.services.AttendanceService;
import org.cardiffmet.services.AttendanceServiceImpl;
import org.cardiffmet.utils.LogoutHelper;
import org.cardiffmet.utils.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private User user;
    private AttendanceService service;
    private String assignedClass;

    public TeacherDashboard(User user) {
        this.user = user;
        this.service = new AttendanceServiceImpl();
        loadAssignedClass();

        setTitle("Teacher Dashboard - " + user.getName() + " | Class: " + assignedClass);
        setSize(800, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top bar with welcome + logout
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel welcome = new JLabel("Welcome, " + user.getName() + " (Teacher) | Class: " + assignedClass);
        welcome.setFont(new Font("Arial", Font.BOLD, 13));
        topBar.add(welcome, BorderLayout.WEST);
        topBar.add(LogoutHelper.createLogoutButton(this), BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Take Attendance", createAttendancePanel());
        tabs.add("Class Report", createReportPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private void loadAssignedClass() {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT class_name FROM classes WHERE teacher_id=?");
            ps.setString(1, user.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) assignedClass = rs.getString("class_name");
            else assignedClass = "Not Assigned";
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private JPanel createAttendancePanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField dateF = new JTextField(LocalDate.now().toString());
        JPanel top = new JPanel();
        top.add(new JLabel("Date (YYYY-MM-DD):"));
        top.add(dateF);
        JButton loadBtn = new JButton("Load Students");
        top.add(loadBtn);
        p.add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Student ID", "Name", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 2; }
        };
        JTable table = new JTable(model);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Present", "Absent"});
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(statusCombo));
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton saveBtn = new JButton("Save Attendance");
        p.add(saveBtn, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            if (assignedClass.equals("Not Assigned")) {
                JOptionPane.showMessageDialog(this, "No class assigned to you");
                return;
            }
            model.setRowCount(0);
            try {
                Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT user_id, name FROM users WHERE class_name=? AND role='STUDENT'");
                ps.setString(1, assignedClass);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    model.addRow(new Object[]{rs.getString("user_id"),
                            rs.getString("name"), "Present"});
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        saveBtn.addActionListener(e -> {
            try {
                String date = dateF.getText().trim();
                ValidationUtil.validateDate(date);
                int saved = 0;
                for (int i = 0; i < model.getRowCount(); i++) {
                    String sid = model.getValueAt(i, 0).toString();
                    String status = model.getValueAt(i, 2).toString();
                    if (service.markAttendance(sid, assignedClass, date, status)) saved++;
                }
                JOptionPane.showMessageDialog(this, "Saved attendance for " + saved + " students");
            } catch (ValidationException | AttendanceException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private JPanel createReportPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "StudentID", "Name", "Date", "Status", "Validated"}, 0);
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Report for " + assignedClass);
        p.add(refreshBtn, BorderLayout.NORTH);

        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<String[]> data = service.getAttendanceByClass(assignedClass);
            for (String[] row : data) model.addRow(row);
        });

        return p;
    }
}
