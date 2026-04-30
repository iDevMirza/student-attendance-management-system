package org.cardiffmet.utils;

import org.cardiffmet.exceptions.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern ID_PATTERN =
            Pattern.compile("^[A-Za-z0-9]{3,10}$");

    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    public static void validateId(String id, String fieldName) throws ValidationException {
        validateNotEmpty(id, fieldName);
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new ValidationException(fieldName + " must be 3-10 alphanumeric characters");
        }
    }

    public static void validateDate(String date) throws ValidationException {
        validateNotEmpty(date, "Date");
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            throw new ValidationException("Date must be in YYYY-MM-DD format");
        }
    }

    public static void validateName(String name) throws ValidationException {
        validateNotEmpty(name, "Name");
        if (name.length() < 2) {
            throw new ValidationException("Name must be at least 2 characters");
        }
    }
}
