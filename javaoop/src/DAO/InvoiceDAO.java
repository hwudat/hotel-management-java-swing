package DAO;

import models.Invoice;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

   
    public boolean createInvoice(Invoice inv) {
        return false; 
    }

    public Object[] getInvoiceHeaderInfo(String roomId) {
        String sql = "SELECT TOP 1 c.full_name, r.room_number, rt.type_name, rt.price_per_night, b.check_in_date, b.check_out_date " +
                     "FROM Booking b " +
                     "JOIN Customer c ON b.customer_id = c.customer_id " +
                     "JOIN Room r ON b.room_id = r.room_id " +
                     "JOIN RoomType rt ON r.type_id = rt.type_id " +
                     "WHERE b.room_id = ? " +
                     "ORDER BY b.booking_id DESC"; 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String cusName = rs.getString("full_name");
                String roomNum = rs.getString("room_number");
                String typeName = rs.getString("type_name");
                double price = rs.getDouble("price_per_night");
                Timestamp in = rs.getTimestamp("check_in_date");
                Timestamp out = rs.getTimestamp("check_out_date");
                
                long diff = (out != null ? out.getTime() : System.currentTimeMillis()) - in.getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                if (days == 0) days = 1;

                return new Object[]{cusName, roomNum, typeName, price, days};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}