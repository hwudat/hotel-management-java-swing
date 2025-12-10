package Service;

import DAO.RoomDAO;
import models.Room;
import java.util.List;

public class RoomService {
    private RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
    }

    public List<Room> getRoomList() {
        return roomDAO.getAllRooms();
    }
}