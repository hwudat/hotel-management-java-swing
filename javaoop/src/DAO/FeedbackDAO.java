package DAO;

import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    // Khách hàng gửi phản hồi
    public boolean sendFeedback(String customerId, String content, int rating) {
        String sql = "INSERT INTO Feedback(customer_id, content, rating, create_date) VALUES (?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customerId);
            ps.setString(2, content);
            ps.setInt(3, rating);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Manager xem tất cả phản hồi (Dạng String đơn giản để hiển thị)
    public List<String> getAllFeedbacks() {
        List<String> list = new ArrayList<>();
        // Join bảng để lấy tên khách hàng thay vì ID
        String sql = "SELECT c.name, f.content, f.rating, f.create_date " +
                "FROM Feedback f JOIN Customer c ON f.customer_id = c.customer_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String fb = String.format("Khách: %s | %d Sao | Nội dung: %s | Ngày: %s",
                        rs.getString("name"),
                        rs.getInt("rating"),
                        rs.getString("content"),
                        rs.getDate("create_date"));
                list.add(fb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}