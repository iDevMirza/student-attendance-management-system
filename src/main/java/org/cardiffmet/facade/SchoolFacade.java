package org.cardiffmet.facade;

import org.cardiffmet.database.DatabaseConnection;
import org.cardiffmet.exceptions.InvalidUserException;
import org.cardiffmet.exceptions.ValidationException;
import org.cardiffmet.factory.UserFactory;
import org.cardiffmet.models.User;
import org.cardiffmet.observer.AttendanceSubject;
import org.cardiffmet.observer.ParentNotifier;
import org.cardiffmet.services.AttendanceService;
import org.cardiffmet.services.AttendanceServiceImpl;
import org.cardiffmet.utils.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SchoolFacade {
    private Connection conn;
    private AttendanceService attendanceService;
    private AttendanceSubject subject;

    public SchoolFacade() {
        conn = DatabaseConnection.getInstance().getConnection();
        attendanceService = new AttendanceServiceImpl();
        subject = new AttendanceSubject();
        subject.addObserver(new ParentNotifier());
    }

    // Add a new teacher with full validation
    public void addTeacher(String id, String name, String email)
            throws ValidationException, InvalidUserException {
        ValidationUtil.validateId(id, "Teacher ID");
        ValidationUtil.validateName(name);
        ValidationUtil.validateEmail(email);

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(user_id, name, email, password, role) VALUES(?,?,?,?,?)");
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, id);
            ps.setString(5, "TEACHER");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new InvalidUserException("Failed to add teacher: " + e.getMessage());
        }
    }

    // Add a new student with full validation
    public void addStudent(String id, String name, String parentEmail, String className)
            throws ValidationException, InvalidUserException {
        ValidationUtil.validateId(id, "Student ID");
        ValidationUtil.validateName(name);
        ValidationUtil.validateEmail(parentEmail);
        ValidationUtil.validateNotEmpty(className, "Class");

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users(user_id, name, email, password, role, class_name) " +
                            "VALUES(?,?,?,?,?,?)");
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, parentEmail);
            ps.setString(4, id);
            ps.setString(5, "STUDENT");
            ps.setString(6, className);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new InvalidUserException("Failed to add student: " + e.getMessage());
        }
    }

    // Create a class with assigned teacher
    public void createClass(String className, String teacherId)
            throws ValidationException, InvalidUserException {
        ValidationUtil.validateNotEmpty(className, "Class name");
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO classes(class_name, teacher_id) VALUES(?,?)");
            ps.setString(1, className);
            ps.setString(2, teacherId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new InvalidUserException("Failed to create class: " + e.getMessage());
        }
    }

    // Authenticate user — single call hides DB and Factory complexity
    public User authenticate(String id, String password, String role)
            throws InvalidUserException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users WHERE user_id=? AND password=? AND role=?");
            ps.setString(1, id);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return UserFactory.createUser(role,
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("class_name"));
            } else {
                throw new InvalidUserException("Invalid credentials");
            }
        } catch (SQLException e) {
            throw new InvalidUserException("Authentication failed: " + e.getMessage());
        }
    }

    // Notify all parents of a class for a given date
    public int notifyParents(String className, String date) throws ValidationException {
        ValidationUtil.validateDate(date);
        ValidationUtil.validateNotEmpty(className, "Class");
        int count = 0;
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT u.user_id, u.name, u.email, a.status " +
                            "FROM users u LEFT JOIN attendance a " +
                            "ON u.user_id = a.student_id AND a.date=? " +
                            "WHERE u.class_name=? AND u.role='STUDENT'");
            ps.setString(1, date);
            ps.setString(2, className);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                if (status == null) status = "Not Marked";
                subject.notifyObservers(
                        rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        date, status);
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public AttendanceService getAttendanceService() {
        return attendanceService;
    }
}
