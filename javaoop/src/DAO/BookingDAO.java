package DAO;

import models.RoomBooking;
import Utils.DatabaseConnection;
import java.sql.*;

public class BookingDAO {

    public boolean addBooking(RoomBooking booking) {
        String sql = "INSERT INTO RoomBooking(booking_id, room_id, customer_id, check_in, check_out) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, booking.getBookingID());
            ps.setString(2, booking.getRoom().getRoomID());
            ps.setString(3, booking.getCustomer().getCustomerID());
            // Chuyển đổi java.util.Date sang java.sql.Date
            ps.setDate(4, new java.sql.Date(booking.getCheckInDate().getTime()));
            ps.setDate(5, new java.sql.Date(booking.getCheckOutDate().getTime()));

            int result = ps.executeUpdate();

            // Nếu đặt phòng thành công, tự động chuyển trạng thái phòng thành "Đã đặt"
            if (result > 0) {
                new RoomDAO().updateRoomStatus(booking.getRoom().getRoomID(), "Occupied");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}