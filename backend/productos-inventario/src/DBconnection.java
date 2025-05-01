import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static final String URL = "jdbc:postgresql://172.31.22.204:5432/pd-in-ct-su";
    private static final String USER = "root";
    private static final String PASSWORD = "utec";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

