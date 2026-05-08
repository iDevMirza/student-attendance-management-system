package org.cardiffmet.ui;

import org.cardiffmet.database.DatabaseConnection;
import org.cardiffmet.exceptions.AttendanceException;
import org.cardiffmet.exceptions.InvalidUserException;
import org.cardiffmet.exceptions.ValidationException;
import org.cardiffmet.facade.SchoolFacade;
import org.cardiffmet.models.StudentRecord;
import org.cardiffmet.models.User;
import org.cardiffmet.services.StudentRecordService;
import org.cardiffmet.services.StudentRecordServiceImpl;
import org.cardiffmet.utils.LogoutHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class HeadteacherDashboard extends JFrame {
    private User user;
    private SchoolFacade facade;
    private StudentRecordService recordService;

    public HeadteacherDashboard(User user) {
        this.user = user;
        this.facade = new SchoolFacade();
        this.recordService = new StudentRecordServiceImpl();

        setTitle("Headteacher Dashboard - " + user.getName());
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top bar with welcome message + logout button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel welcome = new JLabel("Welcome, " + user.getName() + " (Headteacher)");
        welcome.setFont(new Font("Arial", Font.BOLD, 13));
        topBar.add(welcome, BorderLayout.WEST);
        topBar.add(LogoutHelper.createLogoutButton(this), BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Add Teacher", createAddTeacherPanel());
        tabs.add("Add Student", createAddStudentPanel());
        tabs.add("Create Class", createClassPanel());
        tabs.add("Validate/Amend Attendance", createValidatePanel());
        tabs.add("Notify Parents", createNotifyPanel());
        tabs.add("Reports", createReportPanel());
        tabs.add("Student Records", createRecordsPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createAddTeacherPanel() {
        JPanel p = new JPanel(new GridLayout(5, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idF = new JTextField();
        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();

        p.add(new JLabel("Teacher ID:")); p.add(idF);
        p.add(new JLabel("Name:")); p.add(nameF);
        p.add(new JLabel("Email:")); p.add(emailF);
        p.add(new JLabel("Password:")); p.add(new JLabel("(same as Teacher ID)"));

        JButton btn = new JButton("Add Teacher");
        p.add(new JLabel()); p.add(btn);

        btn.addActionListener(e -> {
            try {
                facade.addTeacher(idF.getText().trim(), nameF.getText().trim(), emailF.getText().trim());
                JOptionPane.showMessageDialog(this, "Teacher added!");
                idF.setText(""); nameF.setText(""); emailF.setText("");
            } catch (ValidationException | InvalidUserException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private JPanel createAddStudentPanel() {
        JPanel p = new JPanel(new GridLayout(6, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField idF = new JTextField();
        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();
        JComboBox<String> classBox = new JComboBox<>();
        loadClassesIntoCombo(classBox);

        p.add(new JLabel("Student ID:")); p.add(idF);
        p.add(new JLabel("Name:")); p.add(nameF);
        p.add(new JLabel("Parent Email:")); p.add(emailF);
        p.add(new JLabel("Password:")); p.add(new JLabel("(same as Student ID)"));
        p.add(new JLabel("Assign Class:")); p.add(classBox);

        JButton btn = new JButton("Add Student");
        p.add(new JLabel()); p.add(btn);

        btn.addActionListener(e -> {
            try {
                String cls = (String) classBox.getSelectedItem();
                facade.addStudent(idF.getText().trim(), nameF.getText().trim(),
                        emailF.getText().trim(), cls);
                JOptionPane.showMessageDialog(this, "Student added!");
                idF.setText(""); nameF.setText(""); emailF.setText("");
            } catch (ValidationException | InvalidUserException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private JPanel createClassPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField classF = new JTextField();
        JComboBox<String> teacherBox = new JComboBox<>();
        loadTeachersIntoCombo(teacherBox);

        top.add(new JLabel("Class Name:")); top.add(classF);
        top.add(new JLabel("Assign Teacher:")); top.add(teacherBox);

        JButton btn = new JButton("Create Class");
        top.add(new JLabel()); top.add(btn);
        p.add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Class", "Teacher ID"}, 0);
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        loadClassesIntoTable(model);

        btn.addActionListener(e -> {
            try {
                facade.createClass(classF.getText().trim(),
                        (String) teacherBox.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Class created!");
                classF.setText("");
                loadClassesIntoTable(model);
            } catch (ValidationException | InvalidUserException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private JPanel createValidatePanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> classBox = new JComboBox<>();
        loadClassesIntoCombo(classBox);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "StudentID", "Name", "Date", "Status", "Validated"}, 0);
        JTable table = new JTable(model);

        JPanel top = new JPanel();
        top.add(new JLabel("Class:"));
        top.add(classBox);
        JButton loadBtn = new JButton("Load");
        top.add(loadBtn);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton validateBtn = new JButton("Validate Selected");
        JButton amendBtn = new JButton("Amend Selected");
        bottom.add(validateBtn);
        bottom.add(amendBtn);
        p.add(bottom, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            model.setRowCount(0);
            String cls = (String) classBox.getSelectedItem();
            if (cls != null) {
                List<String[]> data = facade.getAttendanceService().getAttendanceByClass(cls);
                for (String[] row : data) model.addRow(row);
            }
        });

        validateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                    facade.getAttendanceService().validateAttendance(id);
                    JOptionPane.showMessageDialog(this, "Validated!");
                    loadBtn.doClick();
                } catch (AttendanceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        amendBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                    String[] options = {"Present", "Absent"};
                    String newStatus = (String) JOptionPane.showInputDialog(this,
                            "New status:", "Amend", JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);
                    if (newStatus != null) {
                        facade.getAttendanceService().amendAttendance(id, newStatus);
                        JOptionPane.showMessageDialog(this, "Amended!");
                        loadBtn.doClick();
                    }
                } catch (AttendanceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return p;
    }

    private JPanel createNotifyPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> classBox = new JComboBox<>();
        loadClassesIntoCombo(classBox);
        JTextField dateF = new JTextField(java.time.LocalDate.now().toString());

        JPanel top = new JPanel(new GridLayout(2, 2, 10, 10));
        top.add(new JLabel("Class:")); top.add(classBox);
        top.add(new JLabel("Date (YYYY-MM-DD):")); top.add(dateF);
        p.add(top, BorderLayout.NORTH);

        JButton notifyBtn = new JButton("Send Notifications to Parents");
        p.add(notifyBtn, BorderLayout.SOUTH);

        notifyBtn.addActionListener(e -> {
            try {
                int count = facade.notifyParents((String) classBox.getSelectedItem(),
                        dateF.getText().trim());
                JOptionPane.showMessageDialog(this, count + " notifications sent");
            } catch (ValidationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }

    private JPanel createReportPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Monthly", "Weekly"});
        JTextField f1 = new JTextField(8);
        JTextField f2 = new JTextField(8);
        JLabel l1 = new JLabel("Month(MM):");
        JLabel l2 = new JLabel("Year(YYYY):");

        top.add(new JLabel("Type:")); top.add(typeBox);
        top.add(l1); top.add(f1);
        top.add(l2); top.add(f2);

        JButton genBtn = new JButton("Generate");
        top.add(genBtn);
        p.add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"StudentID", "Name", "Class", "Present", "Absent", "%"}, 0);
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        typeBox.addActionListener(e -> {
            if (typeBox.getSelectedItem().equals("Monthly")) {
                l1.setText("Month(MM):");
                l2.setText("Year(YYYY):");
            } else {
                l1.setText("Start(YYYY-MM-DD):");
                l2.setText("End(YYYY-MM-DD):");
            }
        });

        genBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<String[]> data;
            if (typeBox.getSelectedItem().equals("Monthly")) {
                data = facade.getAttendanceService().getMonthlyReport(
                        f1.getText().trim(), f2.getText().trim());
            } else {
                data = facade.getAttendanceService().getWeeklyReport(
                        f1.getText().trim(), f2.getText().trim());
            }
            for (String[] row : data) model.addRow(row);
        });

        return p;
    }

    private JPanel createRecordsPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new GridLayout(2, 4, 10, 10));
        JTextField sidF = new JTextField();
        JTextField subjF = new JTextField();
        JTextField gradeF = new JTextField();
        JTextField healthF = new JTextField();

        top.add(new JLabel("Student ID:")); top.add(sidF);
        top.add(new JLabel("Subject:")); top.add(subjF);
        top.add(new JLabel("Grade:")); top.add(gradeF);
        top.add(new JLabel("Health Notes:")); top.add(healthF);
        p.add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"StudentID", "Subject", "Grade", "Health"}, 0);
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton addBtn = new JButton("Add Record");
        JButton viewBtn = new JButton("View Records");
        bottom.add(addBtn); bottom.add(viewBtn);
        p.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            StudentRecord r = new StudentRecord(sidF.getText().trim(),
                    subjF.getText().trim(), gradeF.getText().trim(), healthF.getText().trim());
            if (recordService.addRecord(r)) {
                JOptionPane.showMessageDialog(this, "Record added!");
            }
        });

        viewBtn.addActionListener(e -> {
            model.setRowCount(0);
            List<StudentRecord> records = recordService.getRecordsByStudent(sidF.getText().trim());
            for (StudentRecord r : records) {
                model.addRow(new Object[]{r.getStudentId(), r.getSubject(),
                        r.getGrade(), r.getHealthNotes()});
            }
        });

        return p;
    }

    // Helpers
    private void loadClassesIntoCombo(JComboBox<String> box) {
        box.removeAllItems();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT class_name FROM classes");
            while (rs.next()) box.addItem(rs.getString("class_name"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadTeachersIntoCombo(JComboBox<String> box) {
        box.removeAllItems();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery(
                    "SELECT user_id FROM users WHERE role='TEACHER'");
            while (rs.next()) box.addItem(rs.getString("user_id"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadClassesIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM classes");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString("class_name"), rs.getString("teacher_id")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
