package DAO;

import Utils.DatabaseConnection;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class BookingDAO {

    public boolean checkIn(int customerId, String roomId, int employeeId) {
        Connection conn = null;
        PreparedStatement psBooking = null;
        PreparedStatement psRoom = null;

        String sqlBooking = "INSERT INTO Booking(customer_id, room_id, employee_id, check_in_date, status) VALUES (?, ?, ?, GETDATE(), 'Checked In')";
        String sqlRoom = "UPDATE Room SET status = 'Occupied' WHERE room_id = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 
            psBooking = conn.prepareStatement(sqlBooking);
            psBooking.setInt(1, customerId);
            psBooking.setString(2, roomId);
            psBooking.setInt(3, employeeId);
            psBooking.executeUpdate();
            psRoom = conn.prepareStatement(sqlRoom);
            psRoom.setString(1, roomId);
            psRoom.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        } finally {
            try { if (psBooking != null) psBooking.close(); } catch (Exception e) {}
            try { if (psRoom != null) psRoom.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return false;
    }

    public boolean checkOut(String roomId) {
        String sqlUpdateBooking = "UPDATE Booking SET status = 'Checked Out' WHERE room_id = ? AND status = 'Checked In'";
        String sqlUpdateRoom = "UPDATE Room SET status = 'Available' WHERE room_id = ?";

        // [MỚI] Câu lệnh để xóa phòng trong bảng Customer (cho sạch dữ liệu)
        String sqlCleanCustomer = "UPDATE Customer SET room_id = NULL WHERE room_id = ?";

        java.sql.Connection conn = null;
        java.sql.PreparedStatement ps1 = null;
        java.sql.PreparedStatement ps2 = null;
        java.sql.PreparedStatement ps3 = null; // [MỚI]

        try {
            conn = Utils.DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Cập nhật Booking
            ps1 = conn.prepareStatement(sqlUpdateBooking);
            ps1.setString(1, roomId);
            int rows = ps1.executeUpdate();

            if (rows > 0) {
                // 2. Trả phòng (Available)
                ps2 = conn.prepareStatement(sqlUpdateRoom);
                ps2.setString(1, roomId);
                ps2.executeUpdate();

                // 3. [MỚI] Xóa vết tích phòng trong hồ sơ khách hàng
                ps3 = conn.prepareStatement(sqlCleanCustomer);
                ps3.setString(1, roomId);
                ps3.executeUpdate();

                conn.commit(); // Lưu tất cả
                return true;
            } else {
                return false; // Không tìm thấy đơn đặt phòng nào để check-out
            }

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            // Đóng kết nối
            try { if (ps1 != null) ps1.close(); } catch (Exception e) {}
            try { if (ps2 != null) ps2.close(); } catch (Exception e) {}
            try { if (ps3 != null) ps3.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
    public void searchAvailableRooms(DefaultTableModel model, String typeFilter) {
        model.setRowCount(0); 
        
        String sql = "SELECT r.room_id, rt.type_name, rt.price_per_night, rt.description " +
                     "FROM Room r " +
                     "JOIN RoomType rt ON r.type_id = rt.type_id " +
                     "WHERE r.status = 'Available' OR r.status = N'Phòng trống'"; 

        if (!typeFilter.equalsIgnoreCase("Tất cả")) {
            sql += " AND rt.type_name = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!typeFilter.equalsIgnoreCase("Tất cả")) {
                ps.setString(1, typeFilter);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("room_id"));
                row.add(rs.getString("type_name"));
                row.add(String.format("%,.0f", rs.getDouble("price_per_night")));
                row.add(rs.getString("description"));
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int findOrCreateCustomer(String name, String phone, String cccd) {
        int customerId = -1;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
        
            String checkSql = "SELECT customer_id FROM Customer WHERE phone = ? OR identity_card = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setString(1, phone);
            psCheck.setString(2, cccd);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                return rs.getInt("customer_id");
            }

            String insertSql = "INSERT INTO Customer(full_name, phone, identity_card) VALUES (?, ?, ?)";
            PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, name);
            psInsert.setString(2, phone);
            psInsert.setString(3, cccd);
            
            int affectedRows = psInsert.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = psInsert.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customerId = generatedKeys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) conn.close(); } catch(Exception e){}
        }
        return customerId;
    }

    public boolean addBooking(int customerId, String roomId, java.util.Date checkIn, java.util.Date checkOut, double deposit) {

        // CÂU LỆNH SQL
        // Bạn hãy kiểm tra kỹ tên các cột trong bảng Booking của bạn có đúng như thế này không:
        // customer_id, room_id, check_in_date, check_out_date, deposit, status, employee_id
        String sqlBooking = "INSERT INTO Booking(customer_id, room_id, check_in_date, check_out_date, deposit, status, employee_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlRoom = "UPDATE Room SET status = 'Occupied' WHERE room_id = ?";
        String sqlCustomer = "UPDATE Customer SET room_id = ? WHERE customer_id = ?";

        java.sql.Connection conn = null;
        java.sql.PreparedStatement psBooking = null;
        java.sql.PreparedStatement psRoom = null;
        java.sql.PreparedStatement psCustomer = null;

        try {
            conn = Utils.DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // --- BƯỚC 1: TẠO BOOKING ---
            psBooking = conn.prepareStatement(sqlBooking);
            psBooking.setInt(1, customerId);
            psBooking.setString(2, roomId);
            psBooking.setTimestamp(3, new java.sql.Timestamp(checkIn.getTime()));
            psBooking.setTimestamp(4, new java.sql.Timestamp(checkOut.getTime()));
            psBooking.setDouble(5, deposit);
            psBooking.setString(6, "Checked In"); // Trạng thái

            // [LƯU Ý QUAN TRỌNG]
            // Số 1 ở đây là ID nhân viên thực hiện (Admin).
            // Đảm bảo trong bảng Employee của bạn có nhân viên id = 1.
            psBooking.setInt(7, 1);

            psBooking.executeUpdate();

            // --- BƯỚC 2: ĐỔI TRẠNG THÁI PHÒNG ---
            psRoom = conn.prepareStatement(sqlRoom);
            psRoom.setString(1, roomId);
            psRoom.executeUpdate();

            // --- BƯỚC 3: GẮN SỐ PHÒNG VÀO KHÁCH HÀNG ---
            psCustomer = conn.prepareStatement(sqlCustomer);
            psCustomer.setString(1, roomId);
            psCustomer.setInt(2, customerId);
            psCustomer.executeUpdate();

            conn.commit(); // Lưu tất cả
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}

            // --- [HIỆN LỖI CHI TIẾT RA MÀN HÌNH] ---
            e.printStackTrace(); // In lỗi ra Console bên dưới
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Lỗi SQL chi tiết: \n" + e.getMessage(),
                    "Lỗi Database",
                    javax.swing.JOptionPane.ERROR_MESSAGE);

            return false;
        } finally {
            try { if (psBooking != null) psBooking.close(); } catch (Exception e) {}
            try { if (psRoom != null) psRoom.close(); } catch (Exception e) {}
            try { if (psCustomer != null) psCustomer.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
    // Thêm khách hàng với đầy đủ thông tin
    public int findOrCreateCustomerFull(String name, String phone, String cccd, String gender, String address) {
        // Bước 1: Tìm xem có chưa
        int existingId = new DAO.CustomerDAO().findCustomerIdByPhone(phone);
        if (existingId != -1) return existingId;

        // Bước 2: Nếu chưa có -> Thêm mới
        // [SỬA LỖI TẠI ĐÂY] Đổi 'gender' thành 'sex'
        String sql = "INSERT INTO Customer(full_name, phone, identity_card, sex, address) VALUES (?, ?, ?, ?, ?)";

        try (java.sql.Connection conn = Utils.DatabaseConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, cccd);
            ps.setString(4, gender);
            ps.setString(5, address);

            ps.executeUpdate();

            // Lấy ID vừa sinh ra
            java.sql.ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            System.out.println("Lỗi SQL Insert Customer: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
}