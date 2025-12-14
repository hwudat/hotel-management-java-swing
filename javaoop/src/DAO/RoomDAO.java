package DAO;

import models.Room;
import models.RoomType;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT r.room_id, r.room_number, r.status, rt.type_id, rt.type_name, rt.price_per_night " +
                "FROM Room r JOIN RoomType rt ON r.type_id = rt.type_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RoomType type = new RoomType(
                        rs.getString("type_id"),
                        rs.getString("type_name"),
                        rs.getDouble("price_per_night")
                );
                list.add(new Room(rs.getString("room_id"), type, rs.getString("status")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateRoomStatus(String roomId, String status) {
        String sql = "UPDATE Room SET status = ? WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}