package DAO;

import models.Customer;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class CustomerDAO {

    // 1. Hàm load danh sách khách hàng
    public void loadCustomerData(javax.swing.table.DefaultTableModel model, String keyword) {
        model.setRowCount(0);

        // SQL lấy thông tin khách hàng + số phòng (nếu đang thuê)
        String sql = "SELECT c.customer_id, c.full_name, c.identity_card, c.sex, c.phone, c.address, r.room_number " +
                "FROM Customer c " +
                "LEFT JOIN Booking b ON c.customer_id = b.customer_id AND b.status IN ('Occupied', 'Checked In', 'Reserved', 'Đang ở') " +
                "LEFT JOIN Room r ON b.room_id = r.room_id " +
                "WHERE c.full_name LIKE ? OR c.phone LIKE ?";

        try (java.sql.Connection conn = Utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);

            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                java.util.Vector<Object> row = new java.util.Vector<>();
                row.add(rs.getInt("customer_id"));

                // Xử lý hiển thị số phòng
                String roomNum = rs.getString("room_number");
                if (roomNum == null) {
                    row.add("---");
                } else {
                    row.add(roomNum);
                }

                row.add(rs.getString("full_name"));
                row.add(rs.getString("identity_card"));
                row.add(rs.getString("sex"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("address")); // Lấy địa chỉ từ DB

                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. Hàm thêm khách hàng
    public boolean addCustomer(Customer c) {
        String sql = "INSERT INTO Customer(room_id, full_name, identity_card, sex, phone, address) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getRoomId());
            ps.setString(2, c.getFullName());
            ps.setString(3, c.getIdentityCard());
            ps.setString(4, c.getGender());
            ps.setString(5, c.getPhone());

            // [ĐÃ SỬA] Thay getNationality bằng getAddress
            ps.setString(6, c.getAddress());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Hàm cập nhật khách hàng
    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE Customer SET room_id=?, full_name=?, identity_card=?, sex=?, phone=?, address=? WHERE customer_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getRoomId());
            ps.setString(2, c.getFullName());
            ps.setString(3, c.getIdentityCard());
            ps.setString(4, c.getGender());
            ps.setString(5, c.getPhone());

            // [ĐÃ SỬA] Thay getNationality bằng getAddress
            ps.setString(6, c.getAddress());

            ps.setInt(7, c.getCustomerId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Hàm xóa khách hàng
    public boolean deleteCustomer(int id) {
        String sql = "DELETE FROM Customer WHERE customer_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public int findCustomerIdByPhone(String phone) {
        String sql = "SELECT customer_id FROM Customer WHERE phone = ?";
        try (java.sql.Connection conn = Utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    // Hàm lấy đầy đủ thông tin khách để điền vào form
    public models.Customer getCustomerByPhone(String phone) {
        String sql = "SELECT * FROM Customer WHERE phone = ?";
        try (java.sql.Connection conn = Utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                models.Customer c = new models.Customer();
                c.setCustomerId(rs.getInt("customer_id"));
                c.setFullName(rs.getString("full_name"));
                c.setIdentityCard(rs.getString("identity_card"));

                // [LƯU Ý] Cột trong Database của bạn là 'sex'
                c.setGender(rs.getString("sex"));

                c.setAddress(rs.getString("address"));
                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}