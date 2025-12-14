package DAO;

import Utils.DatabaseConnection; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public int getRoomCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Room WHERE status = ?";
        if (status.equals("TOTAL")) {
            sql = "SELECT COUNT(*) FROM Room";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (!status.equals("TOTAL")) {
                ps.setString(1, status);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Object[][] getRecentActivities() {
        String sql = "SELECT TOP 5 c.full_name, b.room_id, b.check_in_date, b.status " +
                     "FROM Booking b " +
                     "JOIN Customer c ON b.customer_id = c.customer_id " +
                     "ORDER BY b.booking_id DESC"; 

        List<Object[]> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("full_name");
                String room = rs.getString("room_id");
               
                Timestamp ts = rs.getTimestamp("check_in_date");
                String time = (ts != null) ? ts.toString().substring(0, 16) : "N/A";
                
                String status = rs.getString("status");

                list.add(new Object[]{name, room, time, status});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[][] data = new Object[list.size()][4];
        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i);
        }
        return data;
    }
}