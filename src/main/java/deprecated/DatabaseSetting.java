package deprecated;



import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseSetting {

    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/tssu?zeroDateTimeBehavior=convertToNull";
    public static final String USER = "root";
    public static final String PASSWORD = "netterkerl21";   
    private DatabaseSetting() {
    }
}
