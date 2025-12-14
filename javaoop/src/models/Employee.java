package models;

public class Employee {
    private int employeeId; 
    private String fullName;
    private String position; 
    private String gender;
    private String phone;
    private String email;
    private double salary;

    public Employee() {}

    public Employee(int employeeId, String fullName, String position, String gender, String phone, String email, double salary) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.position = position;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
}