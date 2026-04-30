package org.cardiffmet.models;

public class Teacher extends User {
    private String assignedClass;

    public Teacher(String userId, String name, String email, String password) {
        super(userId, name, email, password);
    }

    @Override
    public String getRole() { return "TEACHER"; }

    public String getAssignedClass() { return assignedClass; }
    public void setAssignedClass(String assignedClass) { this.assignedClass = assignedClass; }
}
