package DAO;

import Utils.DatabaseConnection;
import java.sql.*;

public class BookingDetailDAO {

    public boolean addServiceToBooking(String bookingID, String serviceID, int quantity, double price) {
        String sql = "INSERT INTO BookingDetail(booking_id, service_id, quantity, price_at_booking) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bookingID);
            ps.setString(2, serviceID);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalServicePrice(String bookingID) {
        String sql = "SELECT SUM(quantity * price_at_booking) FROM BookingDetail WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bookingID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}