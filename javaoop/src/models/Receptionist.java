package models;

public class Receptionist {
    private String receptionistID;
    private String namePersonal;
    private String phoneNum;
    private Account account; // Quan hệ 1-1

    public Receptionist(String id, String name, String phone, Account account) {
        this.receptionistID = id;
        this.namePersonal = name;
        this.phoneNum = phone;
        this.account = account;
    }

    public Receptionist(String receptionistID, String namePersonal, String phoneNum) {
        this.receptionistID = receptionistID;
        this.namePersonal = namePersonal;
        this.phoneNum = phoneNum;
    }
    public void createBooking(Customer customer, Room room) {
        System.out.println("Receptionist created booking for " + customer.getNameCustomer());
    }

    public Invoice issueInvoice(RoomBooking booking) {
        Invoice inv = new Invoice("INV001", booking.getBookingID(), "Paid",
                booking.calculateTotal());
        return inv;
    }

    public String getReceptionistID() {
        return receptionistID;
    }

    public String getNamePersonal() {
        return namePersonal;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
}