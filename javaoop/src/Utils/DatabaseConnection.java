package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=HotelManagement;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa"; 
    private static final String PASS = "123456"; 

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Kết nối CSDL thành công!");
            
        } catch (ClassNotFoundException ex) {
            System.out.println("Lỗi: Thiếu thư viện JDBC Driver!");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Lỗi: Sai thông tin kết nối hoặc SQL Server chưa bật!");
            ex.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
      
        Connection c = getConnection();
        
        if (c != null) {
            try {
                c.close(); 
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}