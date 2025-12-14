package models;

public class Customer {
    private int customerId; 
    private String fullName;
    private String phone;
    private String identityCard; // CCCD
    private String gender;
    private String nationality;

    public Customer() {
    }

    public Customer(int customerId, String fullName, String phone, String identityCard, String gender, String nationality) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.identityCard = identityCard;
        this.gender = gender;
        this.nationality = nationality;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdentityCard() { return identityCard; }
    public void setIdentityCard(String identityCard) { this.identityCard = identityCard; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
}