package models;

public class RoomType {
    private String typeID;
    private String name; // VIP, Single, Double
    private String description;
    private double basePrice;

    public RoomType(String id, String name, double price) {
        this.typeID = id;
        this.name = name;
        this.basePrice = price;
    }

    public double getPrice() { return basePrice; }
    public String getName() { return name; }
}