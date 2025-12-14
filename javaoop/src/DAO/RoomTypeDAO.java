package DAO;

import models.RoomType;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    public List<RoomType> getAllRoomTypes() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT * FROM RoomType";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RoomType rt = new RoomType(
                    String.valueOf(rs.getInt("type_id")),
                    rs.getString("type_name"),
                    rs.getDouble("price_per_night"),
                    rs.getString("description") 
                );
                list.add(rt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addRoomType(String name, double price, String desc) {
        String sql = "INSERT INTO RoomType (type_name, price_per_night, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, desc); 
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRoomType(int id, String name, double price, String desc) {
        String sql = "UPDATE RoomType SET type_name = ?, price_per_night = ?, description = ? WHERE type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, desc); 
            ps.setInt(4, id);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRoomType(int id) {
        String sql = "DELETE FROM RoomType WHERE type_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}