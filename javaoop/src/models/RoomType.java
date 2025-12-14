package models;

public class RoomType {
    private String typeId;
    private String typeName;
    private double price;
    private String description;

    public RoomType() {
    }

    public RoomType(String typeId, String typeName, double price, String description) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.price = price;
        this.description = description;
    }

    public RoomType(String typeId, String typeName, double price) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.price = price;
        this.description = "";
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}