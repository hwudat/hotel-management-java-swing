package DAO;

import models.Service;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class ServiceDAO {

    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM Service";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Service(
                    rs.getInt("service_id"),
                    rs.getString("service_name"),
                    rs.getDouble("price"),
                    rs.getString("unit"),
                    rs.getString("classify") 
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addServiceUsage(String roomId, int serviceId, int quantity, double total) {
        String sql = "INSERT INTO ServiceUsage(room_id, service_id, quantity, total_price, usage_date) VALUES(?, ?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roomId);
            ps.setInt(2, serviceId);
            ps.setInt(3, quantity);
            ps.setDouble(4, total);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void loadServiceUsageHistory(DefaultTableModel model, String roomId) {
        model.setRowCount(0);
        String sql = "SELECT su.usage_id, su.room_id, s.service_name, su.quantity, s.price, su.total_price, su.usage_date " +
                     "FROM ServiceUsage su " +
                     "JOIN Service s ON su.service_id = s.service_id " +
                     "WHERE su.room_id = ? " +
                     "ORDER BY su.usage_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("room_id"));
                row.add(rs.getString("service_name"));
                row.add(rs.getInt("quantity"));
                row.add(String.format("%,.0f", rs.getDouble("price")));
                row.add(String.format("%,.0f", rs.getDouble("total_price")));
                row.add(rs.getTimestamp("usage_date"));
                
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getCustomerNameByRoom(String roomId) {
        String sql = "SELECT c.full_name FROM Booking b " +
                     "JOIN Customer c ON b.customer_id = c.customer_id " +
                     "WHERE b.room_id = ? AND b.status = 'Checked In'"; 
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getString("full_name");
        } catch(Exception e) { e.printStackTrace(); }
        return "Không có khách / Trống";
    }

    public List<Service> getListServices(String keyword) {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE service_name LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Service(
                    rs.getInt("service_id"),
                    rs.getString("service_name"),
                    rs.getDouble("price"),
                    rs.getString("unit"),
                    rs.getString("classify") 
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addService(Service s) {
        String sql = "INSERT INTO Service(service_name, classify, unit, price) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, s.getName());
            ps.setString(2, s.getCategory());
            ps.setString(3, s.getUnit());
            ps.setDouble(4, s.getPrice());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateService(Service s) {
        String sql = "UPDATE Service SET service_name=?, classify=?, unit=?, price=? WHERE service_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, s.getName());
            ps.setString(2, s.getCategory());
            ps.setString(3, s.getUnit());
            ps.setDouble(4, s.getPrice());
            ps.setInt(5, s.getId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteService(int id) {
        String sql = "DELETE FROM Service WHERE service_id=?";
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