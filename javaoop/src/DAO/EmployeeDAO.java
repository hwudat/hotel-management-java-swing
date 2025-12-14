package DAO;

import models.Employee;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class EmployeeDAO {

    public void loadEmployeeData(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        String sql = "SELECT * FROM Employee WHERE full_name LIKE ? OR phone LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("employee_id"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("position"));
                row.add(rs.getString("sex"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("email"));
                row.add(String.format("%,.0f", rs.getDouble("salary"))); 
                
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addEmployee(Employee emp) {
        String sql = "INSERT INTO Employee(full_name, position, sex, phone, email, salary) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, emp.getFullName());
            ps.setString(2, emp.getPosition());
            ps.setString(3, emp.getGender());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getEmail());
            ps.setDouble(6, emp.getSalary());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE Employee SET full_name=?, position=?, sex=?, phone=?, email=?, salary=? WHERE employee_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, emp.getFullName());
            ps.setString(2, emp.getPosition());
            ps.setString(3, emp.getGender());
            ps.setString(4, emp.getPhone());
            ps.setString(5, emp.getEmail());
            ps.setDouble(6, emp.getSalary());
            ps.setInt(7, emp.getEmployeeId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM Employee WHERE employee_id=?";
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