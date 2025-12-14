package models;

public class Room {
    private String roomID;
    private RoomType roomType; 
    private String style;
    private String status; 
    private double bookingPrice; 

    public Room(String id, RoomType type, String status) {
        this.roomID = id;
        this.roomType = type;
        this.status = status;
        this.bookingPrice = type.getPrice();
    }

    public boolean isRoomAvailable() {
        return "Available".equalsIgnoreCase(this.status);
    }


    public String getRoomID() { 
        return roomID; 
    }

    public double getPrice() { 
        return bookingPrice; 
    }

    public String getStatus() {
        return status;
    }

    public RoomType getRoomType() {
        return roomType;
    }
}