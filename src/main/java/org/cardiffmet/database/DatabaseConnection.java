package org.cardiffmet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:attendance.db";

    private DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            insertDefaultHeadteacher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS classes (" +
                "class_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "class_name TEXT UNIQUE NOT NULL, " +
                "teacher_id TEXT)");

        statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                "user_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "email TEXT, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL, " +
                "class_name TEXT)");

        statement.execute("CREATE TABLE IF NOT EXISTS attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id TEXT, " +
                "class_name TEXT, " +
                "date TEXT, " +
                "status TEXT, " +
                "validated INTEGER DEFAULT 0)");

        // Integration: Student records (grades + health)
        statement.execute("CREATE TABLE IF NOT EXISTS student_records (" +
                "record_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id TEXT, " +
                "subject TEXT, " +
                "grade TEXT, " +
                "health_notes TEXT)");

        statement.close();
    }

    private void insertDefaultHeadteacher() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT OR IGNORE INTO users VALUES " +
                "('HDT01', 'Admin', 'admin@school.com', 'admin123', 'HEADTEACHER', NULL)");
        statement.close();
    }
}
