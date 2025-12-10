package DAO;

import models.Customer;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("id_card")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addCustomer(Customer c) {
        String sql = "INSERT INTO Customer(customer_id, name, phone, id_card, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getCustomerID());
            ps.setString(2, c.getNameCustomer());
            ps.setString(3, c.getPhoneNum());
            ps.setString(4, c.getIdCardNumber());
            ps.setString(5, c.getAddress());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}