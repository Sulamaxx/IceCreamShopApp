package connection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQL {

    private static Connection c;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/icecreamshopdb", "root", "sulochana123");
        } catch (Exception e) {
            e.printStackTrace();
        }

    };
    
    public static ResultSet execute(String query)throws Exception{

        try {

            Statement s = c.createStatement();

            if (query.startsWith("SELECT")) {
                ResultSet resultset = s.executeQuery(query);
                return resultset;
            } else {
                int result = s.executeUpdate(query);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }        
    }  
}