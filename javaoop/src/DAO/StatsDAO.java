package DAO;

import Utils.DatabaseConnection;
import java.sql.*;

public class StatsDAO {

    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM Invoice WHERE status = 'Paid'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countAvailableRooms() {
        String sql = "SELECT COUNT(*) FROM Room WHERE status = 'Available'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}