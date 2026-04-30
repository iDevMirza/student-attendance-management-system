package org.cardiffmet.models;

public class StudentRecord {
    private int recordId;
    private final String studentId;
    private final String subject;
    private final String grade;
    private final String healthNotes;

    public StudentRecord(String studentId, String subject, String grade, String healthNotes) {
        this.studentId = studentId;
        this.subject = subject;
        this.grade = grade;
        this.healthNotes = healthNotes;
    }

    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }
    public String getStudentId() { return studentId; }
    public String getSubject() { return subject; }
    public String getGrade() { return grade; }
    public String getHealthNotes() { return healthNotes; }
}