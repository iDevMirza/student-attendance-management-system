package org.cardiffmet.services;

import org.cardiffmet.exceptions.AttendanceException;

import java.util.List;

public interface AttendanceService {
    boolean markAttendance(String studentId, String className, String date, String status)
            throws AttendanceException;
    List<String[]> getAttendanceByClass(String className);
    List<String[]> getAttendanceByStudent(String studentId);
    boolean validateAttendance(int attendanceId) throws AttendanceException;
    boolean amendAttendance(int attendanceId, String newStatus) throws AttendanceException;
    List<String[]> getMonthlyReport(String month, String year);
    List<String[]> getWeeklyReport(String startDate, String endDate);
}
