package models;

public class Account {
    private String id;
    private String password;
    private String role; // "Manager", "Receptionist", "Customer"

    public Account(String id, String password, String role) {
        this.id = id;
        this.password = password;
        this.role = role;
    }

    public boolean login(String username, String pwd) {
        // Logic kiểm tra đăng nhập
        return this.id.equals(username) && this.password.equals(pwd);
    }

    public void logout() {
        System.out.println("Account " + id + " logged out.");
    }

    public void resetPass(String newPass) {
        this.password = newPass;
    }

    // Getters Setters
    public String getId() { return id; }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}