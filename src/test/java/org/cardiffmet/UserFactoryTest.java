package org.cardiffmet;

import org.cardiffmet.exceptions.InvalidUserException;
import org.cardiffmet.factory.UserFactory;
import org.cardiffmet.models.Headteacher;
import org.cardiffmet.models.Student;
import org.cardiffmet.models.Teacher;
import org.cardiffmet.models.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserFactoryTest {

    @Test
    void testCreateStudent() throws InvalidUserException {
        User u = UserFactory.createUser("STUDENT", "ST001", "Eden Hazard",
                "hazard@gmail.com", "ST001", "Class 1");
        assertInstanceOf(Student.class, u);
        assertEquals("STUDENT", u.getRole());
    }

    @Test
    void testCreateTeacher() throws InvalidUserException {
        User u = UserFactory.createUser("TEACHER", "T001", "Abdul Hamid",
                "hamid@school.com", "T001", null);
        assertInstanceOf(Teacher.class, u);
        assertEquals("TEACHER", u.getRole());
    }

    @Test
    void testCreateHeadteacher() throws InvalidUserException {
        User u = UserFactory.createUser("HEADTEACHER", "HDT01", "Headteacher",
                "headteacher@school.com", "HDT01", null);
        assertInstanceOf(Headteacher.class, u);
        assertEquals("HEADTEACHER", u.getRole());
    }

    @Test
    void testInvalidRoleThrowsException() {
        assertThrows(InvalidUserException.class,
                () -> UserFactory.createUser("PRINCIPAL", "P1", "X",
                        "x@y.com", "p1", null));
    }

    @Test
    void testNullRoleThrowsException() {
        assertThrows(InvalidUserException.class,
                () -> UserFactory.createUser(null, "P1", "X",
                        "x@y.com", "p1", null));
    }
}