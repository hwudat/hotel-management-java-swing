package models;

public class Room {
    private String roomID;
    private RoomType roomType; // Quan hệ n-1
    private String style;
    private String status; // "Available", "Occupied"
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

    // --- CẦN THÊM CÁC HÀM GETTER DƯỚI ĐÂY ĐỂ HẾT LỖI ---

    public String getRoomID() { 
        return roomID; 
    }

    public double getPrice() { 
        return bookingPrice; 
    }

    // Thêm hàm này để Dashboard lấy được trạng thái phòng
    public String getStatus() {
        return status;
    }

    // Thêm hàm này để Dashboard lấy được thông tin loại phòng
    public RoomType getRoomType() {
        return roomType;
    }
}