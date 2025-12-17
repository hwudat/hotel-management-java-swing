package DAO;

import models.Service;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class ServiceDAO {

    // --- PHẦN 1: DÙNG CHO SERVICE MANAGER FORM (QUẢN LÝ DỊCH VỤ) ---

    public List<Service> getListServices(String keyword) {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE service_name LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("service_id"));
                s.setName(rs.getString("service_name"));
                s.setCategory(rs.getString("category"));
                s.setUnit(rs.getString("unit"));
                s.setPrice(rs.getDouble("price"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addService(Service s) {
        String sql = "INSERT INTO Service(service_name, category, unit, price) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCategory());
            ps.setString(3, s.getUnit());
            ps.setDouble(4, s.getPrice());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateService(Service s) {
        String sql = "UPDATE Service SET service_name=?, category=?, unit=?, price=? WHERE service_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCategory());
            ps.setString(3, s.getUnit());
            ps.setDouble(4, s.getPrice());
            ps.setInt(5, s.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteService(int id) {
        String sql = "DELETE FROM Service WHERE service_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // Không xóa được nếu dịch vụ đã được sử dụng (Ràng buộc khóa ngoại)
            e.printStackTrace();
        }
        return false;
    }

    // Hàm dùng chung để lấy tất cả dịch vụ (cho ComboBox)
    public List<Service> getAllServices() {
        return getListServices("");
    }

    // --- PHẦN 2: DÙNG CHO SERVICE USAGE FORM (GHI NHẬN DỊCH VỤ) ---

    // Load lịch sử sử dụng của 1 phòng
    public void loadServiceUsageHistory(DefaultTableModel model, String roomId) {
        model.setRowCount(0);
        String sql = "SELECT s.service_name, u.quantity, s.price, u.total_price, u.usage_date " +
                "FROM ServiceUsage u " +
                "JOIN Service s ON u.service_id = s.service_id " +
                "WHERE u.room_id = ? ORDER BY u.usage_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Vector<Object> row = new Vector<>();
                row.add(roomId);
                row.add(rs.getString("service_name"));
                row.add(rs.getInt("quantity"));
                row.add(String.format("%,.0f", rs.getDouble("price")));
                row.add(String.format("%,.0f", rs.getDouble("total_price")));
                row.add(rs.getTimestamp("usage_date"));
                model.addRow(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Lấy tên khách hàng đang ở phòng đó
    public String getCustomerNameByRoom(String roomId) {
        String sql = "SELECT c.full_name FROM Booking b " +
                "JOIN Customer c ON b.customer_id = c.customer_id " +
                "WHERE b.room_id = ? AND b.status = 'Checked In'"; // Chỉ lấy khách đang ở
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getString("full_name");
        } catch (Exception e) { e.printStackTrace(); }
        return "Trống / Không tìm thấy";
    }

    // Thêm ghi nhận sử dụng dịch vụ

    public boolean addServiceUsage(String roomId, int serviceId, int quantity, double total) {
        String sql = "INSERT INTO ServiceUsage(room_id, service_id, quantity, total_price) VALUES(?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DatabaseConnection.getConnection();

            // [QUAN TRỌNG 1] Ép buộc bật chế độ lưu ngay lập tức
            if (conn != null) {
                conn.setAutoCommit(true);
            } else {
                System.out.println(" LỖI: Kết nối Database bị NULL!");
                return false;
            }

            System.out.println("🔄 Đang thử thêm: Phòng=" + roomId + ", ServiceID=" + serviceId + ", SL=" + quantity);

            ps = conn.prepareStatement(sql);
            ps.setString(1, roomId);
            ps.setInt(2, serviceId);
            ps.setInt(3, quantity);
            ps.setDouble(4, total);

            int row = ps.executeUpdate(); // Thực thi lệnh Insert

            // [QUAN TRỌNG 2] In kết quả ra màn hình Console để kiểm tra
            if (row > 0) {
                System.out.println(" THÀNH CÔNG: Đã chèn 1 dòng vào ServiceUsage.");
                return true;
            } else {
                System.out.println("⚠ THẤT BẠI: Không có dòng nào được chèn (row = 0).");
                return false;
            }

        } catch (Exception e) {
            System.out.println("LỖI NGHIÊM TRỌNG KHI SQL:");
            e.printStackTrace(); // In lỗi đỏ lòm ra màn hình console
            return false;
        } finally {
            // Đóng kết nối thủ công để chắc chắn
            try { if(ps != null) ps.close(); if(conn != null) conn.close(); } catch(Exception e) {}
        }
    }

    // Lấy danh sách dịch vụ đã dùng theo Booking ID
    // Hàm lấy danh sách dịch vụ theo BookingID (Dùng cho Form Check-out)
// Trả về List các mảng Object: [Tên dịch vụ, Số lượng, Thành tiền]
    public java.util.List<Object[]> getServicesByBookingId(int bookingId) {
        java.util.List<Object[]> list = new java.util.ArrayList<>();

        // Logic: Lấy từ bảng ServiceUsage (dựa vào room_id của booking đó)
        // Vì Booking gắn với Room, ServiceUsage cũng gắn với Room
        String sql = "SELECT s.service_name, u.quantity, u.total_price " +
                "FROM ServiceUsage u " +
                "JOIN Service s ON u.service_id = s.service_id " +
                "JOIN Booking b ON u.room_id = b.room_id " + // Nối với Booking để chắc chắn đúng phòng
                "WHERE b.booking_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString("service_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }

        return list;
    }
}