package DAO;

import models.Account;
import Utils.DatabaseConnection;
import java.sql.*;

public class AccountDAO {

    // Kiểm tra đăng nhập
    public Account checkLogin(String username, String password) {
        String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Đổi mật khẩu
    public boolean changePassword(String username, String newPass) {
        String sql = "UPDATE Account SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPass);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccountByUsername(String username) {
        String sql = "SELECT * FROM Account WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 2. Gán tham số username vào dấu hỏi chấm (?)
            ps.setString(1, username);

            // 3. Chạy câu lệnh và lấy kết quả
            ResultSet rs = ps.executeQuery();

            // 4. Nếu tìm thấy dữ liệu trong Database
            if (rs.next()) {
                // Đóng gói dữ liệu từ SQL vào trong đối tượng Account và trả về
                return new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. Nếu không tìm thấy hoặc bị lỗi thì trả về null
        return null;
        
    }

// ... (Các phần import và code cũ giữ nguyên)

    // --- THÊM HÀM NÀY VÀO CUỐI CLASS AccountDAO ---
    public boolean addAccount(Account acc) {
        String sql = "INSERT INTO Account(username, password, role) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, acc.getId());       // Username
            ps.setString(2, acc.getPassword()); // Password
            ps.setString(3, acc.getRole());     // Role
            
            return ps.executeUpdate() > 0; // Trả về true nếu thêm thành công

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}