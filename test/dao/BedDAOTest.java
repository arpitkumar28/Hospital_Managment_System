package dao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BedDAOTest {
    private final BedDAO beds = new BedDAO();

    @Test
    void bedNumbersMustBeNonBlankAndFitTheSchema() {
        assertFalse(beds.addBed(1, null));
        assertFalse(beds.addBed(1, "   "));
        assertFalse(beds.addBed(1, "x".repeat(41)));
        assertFalse(BedDAO.isValidBedNumber(null));
        assertFalse(BedDAO.isValidBedNumber(" \t "));
        assertFalse(BedDAO.isValidBedNumber("x".repeat(41)));
        assertTrue(BedDAO.isValidBedNumber(" Bed 1 "));
        assertTrue(BedDAO.isValidBedNumber("x".repeat(40)));
    }

    @Test
    void roomAndBedIdsMustBePositiveBeforeDatabaseAccess() {
        assertFalse(beds.addBed(0, "A1"));
        assertFalse(beds.addBed(-1, "A1"));
        assertFalse(beds.updateBed(0, 1, "A1", "AVAILABLE"));
        assertFalse(beds.updateBed(1, 0, "A1", "AVAILABLE"));
        assertFalse(beds.updateBedStatus(0, "AVAILABLE"));
        assertFalse(beds.deleteBed(0));
    }

    @Test
    void onlySchemaSupportedStatusesAreAcceptedBeforeDatabaseAccess() {
        assertFalse(beds.updateBed(1, 1, "A1", null));
        assertFalse(beds.updateBed(1, 1, "A1", "VACANT"));
        assertFalse(beds.updateBed(1, 1, "A1", "available"));
        assertFalse(beds.updateBedStatus(1, "VACANT"));
        assertFalse(beds.updateBedStatus(1, "available"));
        assertTrue(BedDAO.isValidStatus("AVAILABLE"));
        assertTrue(BedDAO.isValidStatus("OCCUPIED"));
    }
}
