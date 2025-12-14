package DAO;

import models.Account;
import Utils.DatabaseConnection;
import java.sql.*;

public class AccountDAO {

    public Account checkLogin(String username, String password) {
        String sql = "SELECT * FROM Account WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean changePassword(String username, String newPass) {
        String sql = "UPDATE Account SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPass);
            ps.setString(2, username);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccountByUsername(String username) {
        String sql = "SELECT * FROM Account WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
        
    }

    public boolean addAccount(Account acc) {
        String sql = "INSERT INTO Account(username, password, role) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, acc.getId());       
            ps.setString(2, acc.getPassword()); 
            ps.setString(3, acc.getRole());     
            
            return ps.executeUpdate() > 0; 

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}