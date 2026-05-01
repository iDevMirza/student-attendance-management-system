package org.cardiffmet.services;

import org.cardiffmet.models.StudentRecord;

import java.util.List;

public interface StudentRecordService {
    boolean addRecord(StudentRecord record);
    List<StudentRecord> getRecordsByStudent(String studentId);
}
