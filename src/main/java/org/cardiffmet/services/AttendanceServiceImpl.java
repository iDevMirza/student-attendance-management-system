package org.cardiffmet.services;

import org.cardiffmet.database.DatabaseConnection;
import org.cardiffmet.exceptions.AttendanceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceServiceImpl implements AttendanceService {
    private Connection conn;

    public AttendanceServiceImpl() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean markAttendance(String studentId, String className, String date, String status)
            throws AttendanceException {
        try {
            PreparedStatement check = null;
            try {
                check = conn.prepareStatement(
                        "SELECT id FROM attendance WHERE student_id=? AND date=?");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            check.setString(1, studentId);
            check.setString(2, date);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE attendance SET status=? WHERE student_id=? AND date=?");
                update.setString(1, status);
                update.setString(2, studentId);
                update.setString(3, date);
                update.executeUpdate();
            } else {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO attendance(student_id, class_name, date, status) VALUES (?,?,?,?)");
                ps.setString(1, studentId);
                ps.setString(2, className);
                ps.setString(3, date);
                ps.setString(4, status);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            throw new AttendanceException("Failed to mark attendance: " + e.getMessage());
        }
    }

    @Override
    public List<String[]> getAttendanceByClass(String className) {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT a.id, a.student_id, u.name, a.date, a.status, a.validated " +
                            "FROM attendance a JOIN users u ON a.student_id = u.user_id " +
                            "WHERE a.class_name=? ORDER BY a.date DESC");
            ps.setString(1, className);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("date"),
                        rs.getString("status"),
                        rs.getInt("validated") == 1 ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String[]> getAttendanceByStudent(String studentId) {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT date, class_name, status FROM attendance " +
                            "WHERE student_id=? ORDER BY date DESC");
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("date"),
                        rs.getString("class_name"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean validateAttendance(int attendanceId) throws AttendanceException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE attendance SET validated=1 WHERE id=?");
            ps.setInt(1, attendanceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AttendanceException("Failed to validate: " + e.getMessage());
        }
    }

    @Override
    public boolean amendAttendance(int attendanceId, String newStatus) throws AttendanceException {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE attendance SET status=? WHERE id=?");
            ps.setString(1, newStatus);
            ps.setInt(2, attendanceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AttendanceException("Failed to amend: " + e.getMessage());
        }
    }

    @Override
    public List<String[]> getMonthlyReport(String month, String year) {
        List<String[]> list = new ArrayList<>();
        try {
            String pattern = year + "-" + month + "-%";
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT a.student_id, u.name, a.class_name, " +
                            "SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END) as present, " +
                            "SUM(CASE WHEN a.status='Absent' THEN 1 ELSE 0 END) as absent, " +
                            "COUNT(*) as total " +
                            "FROM attendance a JOIN users u ON a.student_id = u.user_id " +
                            "WHERE a.date LIKE ? GROUP BY a.student_id");
            ps.setString(1, pattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                double percent = total > 0 ? (present * 100.0 / total) : 0;
                list.add(new String[]{
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("class_name"),
                        String.valueOf(present),
                        String.valueOf(rs.getInt("absent")),
                        String.format("%.2f%%", percent)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<String[]> getWeeklyReport(String startDate, String endDate) {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT a.student_id, u.name, a.class_name, " +
                            "SUM(CASE WHEN a.status='Present' THEN 1 ELSE 0 END) as present, " +
                            "SUM(CASE WHEN a.status='Absent' THEN 1 ELSE 0 END) as absent, " +
                            "COUNT(*) as total " +
                            "FROM attendance a JOIN users u ON a.student_id = u.user_id " +
                            "WHERE a.date BETWEEN ? AND ? GROUP BY a.student_id");
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int total = rs.getInt("total");
                int present = rs.getInt("present");
                double percent = total > 0 ? (present * 100.0 / total) : 0;
                list.add(new String[]{
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("class_name"),
                        String.valueOf(present),
                        String.valueOf(rs.getInt("absent")),
                        String.format("%.2f%%", percent)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
