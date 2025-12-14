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
        Connection conn = null;
        PreparedStatement psBooking = null;
        PreparedStatement psRoom = null;

        String sqlBooking = "UPDATE Booking SET check_out_date = GETDATE(), status = 'Checked Out' " +
                            "WHERE room_id = ? AND status = 'Checked In'";
        
        String sqlRoom = "UPDATE Room SET status = 'Available' WHERE room_id = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            psBooking = conn.prepareStatement(sqlBooking);
            psBooking.setString(1, roomId);
            int rows = psBooking.executeUpdate();

            if (rows > 0) {
                psRoom = conn.prepareStatement(sqlRoom);
                psRoom.setString(1, roomId);
                psRoom.executeUpdate();
                
                conn.commit();
                return true;
            } else {
                return false; 
            }

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return false;
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
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            String sqlBooking = "INSERT INTO Booking(customer_id, room_id, check_in_date, check_out_date, status, employee_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psBooking = conn.prepareStatement(sqlBooking);
            psBooking.setInt(1, customerId);
            psBooking.setString(2, roomId);
            psBooking.setTimestamp(3, new java.sql.Timestamp(checkIn.getTime()));
            psBooking.setTimestamp(4, new java.sql.Timestamp(checkOut.getTime()));
            psBooking.setString(5, "Checked In"); 
            psBooking.setInt(6, 1); 
            
            psBooking.executeUpdate();

            String sqlRoom = "UPDATE Room SET status = 'Occupied' WHERE room_id = ?";
            PreparedStatement psRoom = conn.prepareStatement(sqlRoom);
            psRoom.setString(1, roomId);
            psRoom.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            try { if(conn != null) conn.rollback(); } catch(Exception ex){}
            e.printStackTrace();
        } finally {
            try { if(conn != null) conn.close(); } catch(Exception e){}
        }
        return false;
    }
}