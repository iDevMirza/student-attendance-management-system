package org.cardiffmet;

import org.cardiffmet.exceptions.ValidationException;
import org.cardiffmet.utils.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {

    @Test
    void testValidEmail() {
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("test@example.com"));
    }

    @Test
    void testInvalidEmail() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateEmail("invalid-email"));
    }

    @Test
    void testEmptyEmail() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateEmail(""));
    }

    @Test
    void testValidId() {
        assertDoesNotThrow(() -> ValidationUtil.validateId("S001", "Student ID"));
    }

    @Test
    void testInvalidIdTooShort() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateId("S1", "Student ID"));
    }

    @Test
    void testInvalidIdSpecialChars() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateId("S@01!", "Student ID"));
    }

    @Test
    void testValidDate() {
        assertDoesNotThrow(() -> ValidationUtil.validateDate("2025-01-15"));
    }

    @Test
    void testInvalidDate() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateDate("15-01-2025"));
    }

    @Test
    void testValidName() {
        assertDoesNotThrow(() -> ValidationUtil.validateName("John"));
    }

    @Test
    void testTooShortName() {
        assertThrows(ValidationException.class,
                () -> ValidationUtil.validateName("J"));
    }
}