package DAO;

import Utils.DatabaseConnection;
import models.Employee;

import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class EmployeeDAO {

    // 1. Load danh sách nhân viên lên bảng
    public void loadData(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        String sql = "SELECT * FROM Employee WHERE full_name LIKE ? OR employee_id LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add("NV" + rs.getInt("employee_id"));
                row.add(rs.getString("full_name"));

                // Try-catch để tránh lỗi nếu DB chưa kịp cập nhật cột mới
                try {
                    row.add(rs.getString("position"));
                    row.add(rs.getString("gender"));
                } catch (SQLException e) {
                    row.add("N/A");
                    row.add("N/A");
                }

                row.add(rs.getString("phone"));
                row.add(rs.getString("email"));

                long salary = rs.getBigDecimal("salary").longValue();
                row.add(String.format("%,d", salary));

                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 2. Thêm nhân viên (Phiên bản chuẩn: Tạo Account -> Tạo Employee)
    // 2. Thêm nhân viên (Phiên bản chuẩn: Tạo Account -> Tạo Employee)
    public boolean addEmployee(String name, String pos, String gender, String phone, String email, double salary) {

        // 1. Tạo username (Cắt từ email + số ngẫu nhiên để không trùng)
        String userPrefix = (email != null && email.contains("@")) ? email.split("@")[0] : "user";
        String uniqueUser = userPrefix + "_" + (System.currentTimeMillis() % 10000);
        if (uniqueUser.length() > 50) uniqueUser = uniqueUser.substring(0, 50);

        // SQL 1: Tạo tài khoản trước (Bắt buộc)
        // [LƯU Ý] Nếu cột pass trong DB là 'pass' thì bạn sửa 'password' thành 'pass' nhé
        String sqlAccount = "INSERT INTO Account(username, password, role) VALUES(?, ?, ?)";

        // SQL 2: Thêm nhân viên sau
        String sqlEmployee = "INSERT INTO Employee(full_name, position, gender, phone, email, salary, username) VALUES(?, ?, ?, ?, ?, ?, ?)";

        java.sql.Connection conn = null;
        java.sql.PreparedStatement psAccount = null;
        java.sql.PreparedStatement psEmployee = null;

        try {
            conn = Utils.DatabaseConnection.getConnection();

            // Tắt chế độ tự lưu để đảm bảo cả 2 lệnh cùng chạy thành công
            conn.setAutoCommit(false);

            // --- BƯỚC 1: INSERT VÀO BẢNG ACCOUNT ---
            psAccount = conn.prepareStatement(sqlAccount);
            psAccount.setString(1, uniqueUser);
            psAccount.setString(2, "123456"); // Mật khẩu mặc định

            // Tự động set quyền
            String role = "Staff";
            if (pos.toLowerCase().contains("quản lý") || pos.toLowerCase().contains("manager")) {
                role = "Manager";
            }
            psAccount.setString(3, role);
            psAccount.executeUpdate();

            // --- BƯỚC 2: INSERT VÀO BẢNG EMPLOYEE ---
            psEmployee = conn.prepareStatement(sqlEmployee);
            psEmployee.setString(1, name);
            psEmployee.setString(2, pos);
            psEmployee.setString(3, gender);
            psEmployee.setString(4, phone);
            psEmployee.setString(5, email);
            psEmployee.setDouble(6, salary);
            psEmployee.setString(7, uniqueUser); // Gắn username vừa tạo vào

            psEmployee.executeUpdate();

            // Lưu thay đổi
            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // Nếu lỗi thì hoàn tác
            } catch (java.sql.SQLException ex) {}

            // In lỗi ra để kiểm tra
            javax.swing.JOptionPane.showMessageDialog(null, "Lỗi: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (psAccount != null) psAccount.close(); } catch (Exception e) {}
            try { if (psEmployee != null) psEmployee.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // 3. Thêm nhân viên (Dùng cho MainTest - Truyền đối tượng Employee)
    public boolean addEmployee(Employee emp) {
        // Gọi lại hàm số 2 để tái sử dụng code
        return addEmployee(
                emp.getFullName(),
                emp.getPosition(),
                emp.getGender(),
                emp.getPhone(),
                emp.getEmail(),
                emp.getSalary()
        );
    }

    // 4. Cập nhật thông tin (Đã sửa đầy đủ tham số)
    public boolean updateEmployee(int id, String name, String pos, String gender, String phone, String email, double salary) {
        String sql = "UPDATE Employee SET full_name=?, position=?, gender=?, phone=?, email=?, salary=? WHERE employee_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, pos);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setDouble(6, salary);

            // Tham số cuối cùng là ID cho mệnh đề WHERE
            ps.setInt(7, id);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi cập nhật nhân viên: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 5. Xóa nhân viên
    // 5. Xóa nhân viên (Cập nhật: Xóa luôn cả Account đi kèm)
    public boolean deleteEmployee(int id) {
        String sqlGetUsername = "SELECT username FROM Employee WHERE employee_id=?";
        String sqlDeleteEmp = "DELETE FROM Employee WHERE employee_id=?";
        String sqlDeleteAcc = "DELETE FROM Account WHERE username=?";

        java.sql.Connection conn = null;
        java.sql.PreparedStatement psGet = null;
        java.sql.PreparedStatement psDelEmp = null;
        java.sql.PreparedStatement psDelAcc = null;

        try {
            conn = Utils.DatabaseConnection.getConnection();

            // [QUAN TRỌNG] Bắt đầu giao dịch để đảm bảo xóa sạch cả 2 bảng
            conn.setAutoCommit(false);

            // BƯỚC 1: Lấy username của nhân viên cần xóa
            psGet = conn.prepareStatement(sqlGetUsername);
            psGet.setInt(1, id);
            java.sql.ResultSet rs = psGet.executeQuery();

            String usernameToDelete = null;
            if (rs.next()) {
                usernameToDelete = rs.getString("username");
            }

            // BƯỚC 2: Xóa Nhân viên trước (để gỡ khóa ngoại)
            psDelEmp = conn.prepareStatement(sqlDeleteEmp);
            psDelEmp.setInt(1, id);
            int rowsAffected = psDelEmp.executeUpdate();

            // BƯỚC 3: Nếu xóa nhân viên thành công -> Xóa tiếp Tài khoản
            if (rowsAffected > 0 && usernameToDelete != null) {
                psDelAcc = conn.prepareStatement(sqlDeleteAcc);
                psDelAcc.setString(1, usernameToDelete);
                psDelAcc.executeUpdate();
            }

            // Lưu thay đổi
            conn.commit();
            return rowsAffected > 0;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {} // Hoàn tác nếu lỗi
            e.printStackTrace();
            return false;
        } finally {
            // Đóng kết nối thủ công
            try { if (psGet != null) psGet.close(); } catch (Exception e) {}
            try { if (psDelEmp != null) psDelEmp.close(); } catch (Exception e) {}
            try { if (psDelAcc != null) psDelAcc.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}