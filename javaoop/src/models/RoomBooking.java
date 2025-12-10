package models;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomBooking {
    private String bookingID;
    private Room room;          // Quan hệ n-1
    private Customer customer;  // Quan hệ n-1
    private Date checkInDate;
    private Date checkOutDate;

    // List này thay cho class RoomBookingService (quan hệ n-n)
    private List<Service> serviceList;

    public RoomBooking(String id, Room r, Customer c, Date in, Date out) {
        this.bookingID = id;
        this.room = r;
        this.customer = c;
        this.checkInDate = in;
        this.checkOutDate = out;
        this.serviceList = new ArrayList<>();
    }

    public void addService(Service s) {
        serviceList.add(s);
        System.out.println("Added service: " + s.getName());
    }

    public double calculateTotal() {
        long diff = checkOutDate.getTime() - checkInDate.getTime();
        long days = diff / (1000 * 60 * 60 * 24); // Tính số ngày
        if (days == 0) days = 1;

        double roomTotal = days * room.getPrice();
        double serviceTotal = 0;
        for (Service s : serviceList) {
            serviceTotal += s.getPrice();
        }
        return roomTotal + serviceTotal;
    }

    public String getBookingID() { return bookingID; }

    public Room getRoom() {
        return this.room;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Date getCheckInDate() {
        return this.checkInDate;
    }

    public Date getCheckOutDate() {
        return this.checkOutDate;
    }
}