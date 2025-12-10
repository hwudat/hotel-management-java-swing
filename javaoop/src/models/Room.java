package models;

public class Room {
    private String roomID;
    private RoomType roomType; // Quan hệ n-1
    private String style;
    private String status; // "Available", "Occupied"
    private double bookingPrice; // Giá thực tế tại thời điểm xem

    public Room(String id, RoomType type, String status) {
        this.roomID = id;
        this.roomType = type;
        this.status = status;
        this.bookingPrice = type.getPrice();
    }


    public boolean isRoomAvailable() {
        return "Available".equalsIgnoreCase(this.status);
    }

    public String getRoomID() { return roomID; }
    public double getPrice() { return bookingPrice; }
}