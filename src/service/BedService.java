package service;

import dao.BedDAO;
import dao.RoomDAO;
import security.AuthorizationService;
import security.SessionManager;

import java.util.List;

/** Session-authorized bed operations. */
public final class BedService extends AuthorizedService {
    private final BedDAO beds;
    private final RoomDAO rooms;

    public BedService() { this(new BedDAO(), new RoomDAO()); }
    public BedService(BedDAO beds, RoomDAO rooms) {
        this(beds, rooms, SessionManager.INSTANCE, new AuthorizationService());
    }
    public BedService(BedDAO beds, RoomDAO rooms, SessionManager sessions, AuthorizationService authorization) {
        super(sessions, authorization);
        this.beds = beds;
        this.rooms = rooms;
    }

    public List<Object[]> getAllRooms() { return execute(AuthorizationService.BEDS, rooms::getAllRooms); }
    public List<Object[]> getAllBeds() { return execute(AuthorizationService.BEDS, beds::getAllBeds); }
    public List<Object[]> getAvailableBeds() { return execute(AuthorizationService.BEDS, beds::getAvailableBeds); }
    public boolean addBed(int roomId, String bedNumber) {
        return execute(AuthorizationService.BEDS, () -> beds.addBed(roomId, bedNumber));
    }
    public boolean updateBed(int bedId, int roomId, String bedNumber, String status) {
        return execute(AuthorizationService.BEDS, () -> beds.updateBed(bedId, roomId, bedNumber, status));
    }
    public boolean updateBedStatus(int bedId, String status) {
        return execute(AuthorizationService.BEDS, () -> beds.updateBedStatus(bedId, status));
    }
    public boolean deleteBed(int bedId) { return execute(AuthorizationService.BEDS, () -> beds.deleteBed(bedId)); }
    public int getBedCount() { return execute(AuthorizationService.BEDS, beds::getBedCount); }
    public int getAvailableBedCount() { return execute(AuthorizationService.BEDS, beds::getAvailableBedCount); }
    public int getOccupiedBedCount() { return execute(AuthorizationService.BEDS, beds::getOccupiedBedCount); }
}
