package models;

public class Service {
    private int id;
    private String name;
    private String category;
    private String unit;
    private double price;

    public Service() {}

    public Service(int id, String name, String category, String unit, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.price = price;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // [QUAN TRỌNG] Hàm này giúp ComboBox hiển thị Tên dịch vụ
    @Override
    public String toString() {
        return this.name;
    }
}