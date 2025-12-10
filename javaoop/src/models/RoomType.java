package models;

public class RoomType {
    private String typeId;
    private String typeName;
    private double price;

    public RoomType(String typeId, String typeName, double price) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.price = price;
    }

    // --- CÁC HÀM GETTER CẦN THIẾT ---
    
    public String getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public double getPrice() {
        return price;
    }
}