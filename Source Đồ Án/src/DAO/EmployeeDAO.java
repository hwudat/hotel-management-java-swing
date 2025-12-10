package DAO;

import models.Receptionist; // Nhớ tạo model này nếu chưa có
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // Lấy danh sách tất cả nhân viên
    public List<Receptionist> getAllEmployees() {
        List<Receptionist> list = new ArrayList<>();
        String sql = "SELECT * FROM Receptionist";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Giả sử model Receptionist có constructor đầy đủ
                Receptionist emp;
                emp = new Receptionist(
                        rs.getString("receptionist_id"),
                        rs.getString("name"),
                        rs.getString("phone")
                );
                list.add(emp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm nhân viên mới
    public boolean addEmployee(Receptionist emp, String usernameAccount) {
        String sql = "INSERT INTO Receptionist(receptionist_id, name, phone, salary, username) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getReceptionistID());
            ps.setString(2, emp.getNamePersonal());
            ps.setString(3, emp.getPhoneNum());
            ps.setString(5, usernameAccount); // Link với tài khoản đã tạo bên bảng Account

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa nhân viên
    public boolean deleteEmployee(String empId) {
        String sql = "DELETE FROM Receptionist WHERE receptionist_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, empId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}