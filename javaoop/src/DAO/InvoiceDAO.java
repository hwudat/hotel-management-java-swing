package DAO;

import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// XÓA DÒNG: import models.InvoiceItem; (Dòng này gây lỗi đỏ)

public class InvoiceDAO {

    // Class nội bộ để chứa dòng hóa đơn (Dùng cái này thay cho InvoiceItem)
    public static class InvoiceLine {
        public String name;
        public String unit;
        public int quantity;
        public double price;
        public double total;

        public InvoiceLine(String name, String unit, int quantity, double price, double total) {
            this.name = name;
            this.unit = unit;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }
    }

    // 1. Hàm lấy toàn bộ chi tiết hóa đơn (Gồm Tiền phòng + Tiền dịch vụ)
    public List<InvoiceLine> getInvoiceDetails(String roomId) {
        List<InvoiceLine> list = new ArrayList<>();

        // --- A. TÍNH TIỀN PHÒNG ---
        String sqlRoom = "SELECT r.room_id, t.price_per_night, b.check_in_date " +
                "FROM Booking b " +
                "JOIN Room r ON b.room_id = r.room_id " +
                "JOIN RoomType t ON r.type_id = t.type_id " +
                "WHERE r.room_id = ? AND b.status = 'Checked In'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlRoom)) {

            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("price_per_night");
                Timestamp checkIn = rs.getTimestamp("check_in_date");

                // Tính số ngày ở
                long diff = System.currentTimeMillis() - checkIn.getTime();
                int days = (int) (diff / (1000 * 60 * 60 * 24));
                if (days == 0) days = 1; // Tối thiểu 1 ngày

                double totalRoom = days * price;
                list.add(new InvoiceLine("Tiền phòng (" + days + " ngày)", "Đêm", days, price, totalRoom));
            }
        } catch (Exception e) { e.printStackTrace(); }

        // --- B. TÍNH TIỀN DỊCH VỤ (Lấy từ bảng ServiceUsage) ---
        String sqlService = "SELECT s.service_name, s.unit, u.quantity, s.price, u.total_price " +
                "FROM ServiceUsage u " +
                "JOIN Service s ON u.service_id = s.service_id " +
                "WHERE u.room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlService)) {

            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new InvoiceLine(
                        rs.getString("service_name"), // Tên dịch vụ (VD: Nước suối)
                        rs.getString("unit"),         // Đơn vị (Chai)
                        rs.getInt("quantity"),        // Số lượng
                        rs.getDouble("price"),        // Đơn giá
                        rs.getDouble("total_price")   // Thành tiền
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }

    // 2. Hàm lấy thông tin khách hàng (Header hóa đơn)
    public String[] getCustomerInfo(String roomId) {
        String[] info = new String[4];
        String sql = "SELECT c.full_name, c.phone, r.room_id, t.type_name " +
                "FROM Booking b " +
                "JOIN Customer c ON b.customer_id = c.customer_id " +
                "JOIN Room r ON b.room_id = r.room_id " +
                "JOIN RoomType t ON r.type_id = t.type_id " +
                "WHERE r.room_id = ? AND b.status = 'Checked In'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info[0] = rs.getString("full_name");
                info[1] = rs.getString("phone");
                info[2] = rs.getString("room_id");
                info[3] = rs.getString("type_name");
                return info;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}