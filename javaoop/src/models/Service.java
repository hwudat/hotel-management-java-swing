package models;

public class Service {
    private int id;
    private String name;
    private double price;
    private String unit;
    private String category;

    public Service() {}

    public Service(int id, String name, double price, String unit, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getUnit() { return unit; }
    public String getCategory() { return category; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return name;
    }
}