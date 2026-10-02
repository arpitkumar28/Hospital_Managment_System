package service;

import dao.RoomDAO;
import security.AuthorizationService;
import security.SessionManager;

import java.util.List;

/** Session-authorized room operations. */
public final class RoomService extends AuthorizedService {
    private final RoomDAO rooms;

    public RoomService() { this(new RoomDAO()); }
    public RoomService(RoomDAO rooms) { this(rooms, SessionManager.INSTANCE, new AuthorizationService()); }
    public RoomService(RoomDAO rooms, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.rooms = rooms;
    }

    public List<Object[]> getAllRooms() { return execute(AuthorizationService.ROOMS, rooms::getAllRooms); }
    public boolean addRoom(String roomNumber, String roomType, double pricePerDay) {
        return execute(AuthorizationService.ROOMS, () -> rooms.addRoom(roomNumber, roomType, pricePerDay));
    }
    public boolean updateRoom(int roomId, String roomNumber, String roomType, double pricePerDay) {
        return execute(AuthorizationService.ROOMS, () -> rooms.updateRoom(roomId, roomNumber, roomType, pricePerDay));
    }
    public boolean deleteRoom(int roomId) { return execute(AuthorizationService.ROOMS, () -> rooms.deleteRoom(roomId)); }
    public boolean roomExists(String roomNumber) { return execute(AuthorizationService.ROOMS, () -> rooms.roomExists(roomNumber)); }
    public int getRoomCount() { return execute(AuthorizationService.ROOMS, rooms::getRoomCount); }
}
