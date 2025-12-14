package models;

public class Account {
    private String id;
    private String password;
    private String role; 

    public Account(String id, String password, String role) {
        this.id = id;
        this.password = password;
        this.role = role;
    }

    public boolean login(String username, String pwd) {
      
        return this.id.equals(username) && this.password.equals(pwd);
    }

    public void logout() {
        System.out.println("Account " + id + " logged out.");
    }

    public void resetPass(String newPass) {
        this.password = newPass;
    }

    public String getId() { return id; }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}