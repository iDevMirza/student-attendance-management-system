package org.cardiffmet.observer;

public interface AttendanceObserver {
    void update(String studentId, String studentName, String parentEmail, String date, String status);
}
