package org.cardiffmet.services;

import org.cardiffmet.database.DatabaseConnection;
import org.cardiffmet.models.StudentRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentRecordServiceImpl implements StudentRecordService {
    private Connection conn;

    public StudentRecordServiceImpl() {
        conn = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public boolean addRecord(StudentRecord record) {
        try {
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(
                        "INSERT INTO student_records(student_id, subject, grade, health_notes) " +
                                "VALUES (?,?,?,?)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ps.setString(1, record.getStudentId());
            ps.setString(2, record.getSubject());
            ps.setString(3, record.getGrade());
            ps.setString(4, record.getHealthNotes());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<StudentRecord> getRecordsByStudent(String studentId) {
        List<StudentRecord> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM student_records WHERE student_id=?");
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StudentRecord r = new StudentRecord(
                        rs.getString("student_id"),
                        rs.getString("subject"),
                        rs.getString("grade"),
                        rs.getString("health_notes"));
                r.setRecordId(rs.getInt("record_id"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
