package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // 1. Thông tin cấu hình (Sửa lại mật khẩu nếu khác)
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=HotelManagement;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa"; 
    private static final String PASS = "123456"; 

    // 2. Hàm tạo kết nối
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Nạp Driver (bắt buộc để Java hiểu SQL Server)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // Mở kết nối
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Kết nối CSDL thành công!");
            
        } catch (ClassNotFoundException ex) {
            System.out.println("Lỗi: Thiếu thư viện JDBC Driver!");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Lỗi: Sai thông tin kết nối hoặc SQL Server chưa bật!");
            ex.printStackTrace();
        }
        
        // Trả về kết nối (để các hàm khác sử dụng)
        return conn;
    }

    // 3. Hàm main để chạy thử ngay tại đây
    public static void main(String[] args) {
        // Gọi hàm kết nối thử
        Connection c = getConnection();
        
        if (c != null) {
            try {
                c.close(); // Thử xong thì đóng lại cho gọn
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}