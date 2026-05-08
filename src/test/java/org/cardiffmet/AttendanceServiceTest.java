package org.cardiffmet;

import org.cardiffmet.exceptions.AttendanceException;
import org.cardiffmet.services.AttendanceService;
import org.cardiffmet.services.AttendanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AttendanceServiceTest {

    private AttendanceService service;

    @BeforeEach
    void setUp() {
        service = new AttendanceServiceImpl();
    }

    @Test
    void testMarkAttendance() throws AttendanceException {
        boolean result = service.markAttendance("TEST_S001", "TestClass",
                "2025-01-15", "Present");
        assertTrue(result);
    }

    @Test
    void testGetAttendanceByStudentReturnsList() {
        assertNotNull(service.getAttendanceByStudent("TEST_S001"));
    }

    @Test
    void testGetMonthlyReportReturnsList() {
        assertNotNull(service.getMonthlyReport("01", "2025"));
    }

    @Test
    void testGetWeeklyReportReturnsList() {
        assertNotNull(service.getWeeklyReport("2025-01-01", "2025-01-31"));
    }
}