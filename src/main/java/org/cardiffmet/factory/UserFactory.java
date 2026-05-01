package org.cardiffmet.factory;

import org.cardiffmet.exceptions.InvalidUserException;
import org.cardiffmet.models.Headteacher;
import org.cardiffmet.models.Student;
import org.cardiffmet.models.Teacher;
import org.cardiffmet.models.User;

public class UserFactory {
    public static User createUser(String role, String userId, String name,
                                  String email, String password, String className)
            throws InvalidUserException {
        if (role == null) {
            throw new InvalidUserException("Role cannot be null");
        }
        return switch (role.toUpperCase()) {
            case "STUDENT" -> new Student(userId, name, email, password, className);
            case "TEACHER" -> new Teacher(userId, name, email, password);
            case "HEADTEACHER" -> new Headteacher(userId, name, email, password);
            default -> throw new InvalidUserException("Unknown role: " + role);
        };
    }
}
