package com.primertrimestre.persistence.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JdbcUtil {

    private static final String URL = "jdbc:mariadb://localhost:3306/registrodb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private JdbcUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
