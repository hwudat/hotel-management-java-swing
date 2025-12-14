package models;

public class Manager {
    private String managerID;
    private String nameManager;
    private String idCardNumber;
    private String phoneNum;
    private Account account; 

    public Manager(String managerID, String name, String idCard, String phone, Account account) {
        this.managerID = managerID;
        this.nameManager = name;
        this.idCardNumber = idCard;
        this.phoneNum = phone;
        this.account = account;
    }

    public void addRoom(Room room) {
        System.out.println("Manager added room: " + room.getRoomID());
     
    }

    public void manageStaff() {
        System.out.println("Managing staff...");
    }

    public void viewReports() {
        System.out.println("Viewing reports...");
    }
}