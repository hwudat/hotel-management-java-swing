package models;

public class Service {
    private String serviceID;
    private String nameService;
    private double priceService;

    public Service(String id, String name, double price) {
        this.serviceID = id;
        this.nameService = name;
        this.priceService = price;
    }

    public double getPrice() { return priceService; }
    public String getName() { return nameService; }
}