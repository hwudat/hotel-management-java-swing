package models;

public class Customer {
    private String customerID;
    private String nameCustomer;
    private String idCardNumber;
    private String phoneNum;
    private String address;
    private Account account; // Quan hệ 1-1

    public Customer(String id, String name, String phone, Account acc) {
        this.customerID = id;
        this.nameCustomer = name;
        this.phoneNum = phone;
        this.account = acc;
    }

    public Customer(String customerID, String nameCustomer, String idCardNumber, String phoneNum) {
        this.customerID = customerID;
        this.nameCustomer = nameCustomer;
        this.idCardNumber = idCardNumber;
        this.phoneNum = phoneNum;
    }

    public String getNameCustomer() { return nameCustomer; }

    public void register() {
        System.out.println("Customer " + nameCustomer + " registered.");
    }

    public void bookRoom(Room room) {
        System.out.println("Customer requested booking for room " + room.getRoomID());
    }

    @Override
    public String toString() {
        return "Customer{" +
                "ID='" + customerID + '\'' +
                ", Tên khách hàng: " + nameCustomer + '\'' +
                ", CCCD: " + idCardNumber + '\'' +
                ", SĐT: " + phoneNum + '\'' +
                ", Địa chỉ: " + address + '\'' +
                '}';
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getAddress() {
        return address;
    }

    public Customer(String address, String nameCustomer, String phoneNum, String idCardNumber, String customerID) {
        this.nameCustomer = nameCustomer;
        this.address = address;
        this.phoneNum = phoneNum;
        this.idCardNumber = idCardNumber;
        this.customerID = customerID;
    }
}