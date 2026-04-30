package org.cardiffmet.models;

public class Student extends User {
    private String className;

    public Student(String userId, String name, String parentEmail, String password, String className) {
        super(userId, name, parentEmail, password);
        this.className = className;
    }

    @Override
    public String getRole() { return "STUDENT"; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
