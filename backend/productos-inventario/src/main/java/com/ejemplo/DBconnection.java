package main.java.com.ejemplo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static final String URL = System.getenv("PG_URL");
    private static final String USER = System.getenv("PG_USER");
    private static final String PASSWORD = System.getenv("PG_PASSWORD");

    public static Connection getConnection() throws SQLException {
        if (URL == null || USER == null || PASSWORD == null) {
            throw new SQLException("❌ Variables de entorno para la base de datos no definidas");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
