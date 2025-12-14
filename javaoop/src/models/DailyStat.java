package models;

public class DailyStat {
    private int day;
    private int bookingCount;
    private double roomRevenue;
    private double serviceRevenue;

    public DailyStat(int day, int bookingCount, double roomRevenue, double serviceRevenue) {
        this.day = day;
        this.bookingCount = bookingCount;
        this.roomRevenue = roomRevenue;
        this.serviceRevenue = serviceRevenue;
    }

    public int getDay() { return day; }
    public int getBookingCount() { return bookingCount; }
    public double getRoomRevenue() { return roomRevenue; }
    public double getServiceRevenue() { return serviceRevenue; }
    public double getTotalRevenue() { return roomRevenue + serviceRevenue; }
    
    public void setServiceRevenue(double serviceRevenue) { this.serviceRevenue = serviceRevenue; }
}