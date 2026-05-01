package org.cardiffmet.observer;

import java.util.ArrayList;
import java.util.List;

public class AttendanceSubject {
    private final List<AttendanceObserver> observers = new ArrayList<>();

    public void addObserver(AttendanceObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AttendanceObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String studentId, String studentName,
                                String parentEmail, String date, String status) {
        for (AttendanceObserver observer : observers) {
            observer.update(studentId, studentName, parentEmail, date, status);
        }
    }
}
