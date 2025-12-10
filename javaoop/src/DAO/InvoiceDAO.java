package DAO;

import models.Invoice;
import Utils.DatabaseConnection;
import java.sql.*;

public class InvoiceDAO {

    public boolean createInvoice(Invoice inv) {
        String sql = "INSERT INTO Invoice(invoice_id, booking_id, total_amount, payment_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, inv.getInvoiceID());
            ps.setString(2, inv.getBookingID());
            ps.setDouble(3, inv.getTotalAmount());
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Ngày hiện tại
            ps.setString(5, "Paid");

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}