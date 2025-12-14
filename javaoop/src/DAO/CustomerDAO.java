package DAO;

import models.Customer;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class CustomerDAO {

    public void loadCustomerData(DefaultTableModel model, String keyword) {
        model.setRowCount(0); 
        
        String sql = "SELECT c.customer_id, c.full_name, c.identity_card, c.sex, c.phone, c.address, " +
                     "r.room_id " + 
                     "FROM Customer c " +
                     "LEFT JOIN Booking b ON c.customer_id = b.customer_id AND b.status = 'Checked In' " +
                     "LEFT JOIN Room r ON b.room_id = r.room_id " +
                     "WHERE c.full_name LIKE ? OR c.phone LIKE ? OR c.identity_card LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("customer_id"));
                
                String room = rs.getString("room_id");
                row.add(room != null ? room : "---");
                
                row.add(rs.getString("full_name"));
                row.add(rs.getString("identity_card"));
                row.add(rs.getString("sex"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("address"));
                
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addCustomer(Customer c) {
        String sql = "INSERT INTO Customer(full_name, identity_card, sex, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getIdentityCard());
            ps.setString(3, c.getGender());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getNationality());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE Customer SET full_name=?, identity_card=?, sex=?, phone=?, address=? WHERE customer_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getIdentityCard());
            ps.setString(3, c.getGender());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getNationality());
            ps.setInt(6, c.getCustomerId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCustomer(int id) {
        String sql = "DELETE FROM Customer WHERE customer_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}