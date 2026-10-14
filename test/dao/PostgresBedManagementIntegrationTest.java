package dao;

import database.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "HOSPITAL_TEST_DB_NAME", matches = "[A-Za-z0-9_]+_test")
class PostgresBedManagementIntegrationTest {
    private static final BedDAO beds = new BedDAO();
    private static final String unique = UUID.randomUUID().toString();
    private static int patientId;
    private static int roomId;
    private static int otherRoomId;
    private static int activeBedId;
    private static int availableBedId;
    private static int admissionId;

    @BeforeAll
    static void setUp() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String currentDatabase;
            try (PreparedStatement statement = connection.prepareStatement("SELECT current_database()");
                 ResultSet result = statement.executeQuery()) {
                assertTrue(result.next());
                currentDatabase = result.getString(1);
            }
            assertEquals(System.getenv("HOSPITAL_TEST_DB_NAME"), currentDatabase,
                    "Bed integration tests require the explicitly named dedicated test database.");

            patientId = insertPatient(connection);
            roomId = insertRoom(connection, "A");
            otherRoomId = insertRoom(connection, "B");
            activeBedId = insertBed(connection, roomId, "active");
            availableBedId = insertBed(connection, roomId, "available");
            admissionId = insertAdmission(connection);
        }
    }

    @BeforeEach
    void resetBedStates() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE beds SET status = CASE WHEN bed_id = ? THEN 'OCCUPIED' ELSE 'AVAILABLE' END " +
                             "WHERE bed_id IN (?, ?)")) {
            statement.setInt(1, activeBedId);
            statement.setInt(2, activeBedId);
            statement.setInt(3, availableBedId);
            statement.executeUpdate();
        }
    }

    @AfterAll
    static void tearDown() throws Exception {
        if (patientId == 0) return;
        try (Connection connection = DatabaseConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM admissions WHERE admission_id = ?")) {
                statement.setInt(1, admissionId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM beds WHERE bed_id IN (?, ?)")) {
                statement.setInt(1, activeBedId);
                statement.setInt(2, availableBedId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM rooms WHERE room_id IN (?, ?)")) {
                statement.setInt(1, roomId);
                statement.setInt(2, otherRoomId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM patients WHERE patient_id = ?")) {
                statement.setInt(1, patientId);
                statement.executeUpdate();
            }
        }
    }

    @Test
    void creationAndUpdateRespectRoomReferencesAndUniqueness() throws Exception {
        assertFalse(beds.addBed(Integer.MAX_VALUE, "missing-room-" + unique));
        assertFalse(beds.addBed(roomId, "active-" + unique));

        assertTrue(beds.addBed(otherRoomId, "valid-" + unique));
        int addedId = bedId(otherRoomId, "valid-" + unique);
        try {
            assertTrue(beds.updateBed(addedId, roomId, "updated-" + unique, "AVAILABLE"));
            assertFalse(beds.updateBed(addedId, Integer.MAX_VALUE, "updated-2-" + unique, "AVAILABLE"));
            assertFalse(beds.updateBed(addedId, roomId, "active-" + unique, "AVAILABLE"));
            assertFalse(beds.updateBedStatus(addedId, "UNSUPPORTED"));
        } finally {
            assertTrue(beds.deleteBed(addedId));
        }
    }

    @Test
    void activeAdmissionProtectsBedAssignmentAndAvailability() throws Exception {
        assertTrue(beds.updateBed(activeBedId, roomId, "active-" + unique, "OCCUPIED"));
        assertFalse(beds.updateBed(activeBedId, otherRoomId, "moved-" + unique, "OCCUPIED"));
        assertFalse(beds.updateBed(activeBedId, roomId, "renamed-" + unique, "OCCUPIED"));
        assertFalse(beds.updateBed(activeBedId, roomId, "active-" + unique, "AVAILABLE"));
        assertFalse(beds.updateBedStatus(activeBedId, "AVAILABLE"));
        assertFalse(beds.deleteBed(activeBedId));

        int availableCountBeforeStaleStatus = beds.getAvailableBedCount();
        assertTrue(beds.getAvailableBeds().stream().anyMatch(row -> id(row[0]) == availableBedId));
        // Even if a stale status is present, an active admission makes this bed unavailable.
        setStatus(activeBedId, "AVAILABLE");
        assertFalse(beds.getAvailableBeds().stream().anyMatch(row -> id(row[0]) == activeBedId));
        assertEquals(availableCountBeforeStaleStatus, beds.getAvailableBedCount());
    }

    @Test
    void bedCanBeDeletedWhenNoAdmissionReferencesIt() throws Exception {
        assertTrue(beds.deleteBed(availableBedId));
        assertFalse(beds.deleteBed(availableBedId));
        assertTrue(insertAvailableBedAgain());
    }

    private static boolean insertAvailableBedAgain() throws Exception {
        int replacement = insertBed(roomId, "replacement-" + unique);
        availableBedId = replacement;
        return replacement > 0;
    }

    private static int id(Object value) { return ((Number) value).intValue(); }

    private static void setStatus(int bedId, String status) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE beds SET status = ? WHERE bed_id = ?")) {
            statement.setString(1, status);
            statement.setInt(2, bedId);
            assertEquals(1, statement.executeUpdate());
        }
    }

    private static int insertPatient(Connection connection) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO patients (name) VALUES (?) RETURNING patient_id")) {
            statement.setString(1, "Bed integration " + unique);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int insertRoom(Connection connection, String suffix) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO rooms (room_number, room_type, price_per_day) VALUES (?, 'Test', 0) RETURNING room_id")) {
            statement.setString(1, "IT-" + suffix + "-" + unique.replace("-", ""));
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int insertBed(Connection connection, int room, String suffix) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO beds (room_id, bed_number) VALUES (?, ?) RETURNING bed_id")) {
            statement.setInt(1, room);
            statement.setString(2, suffix + "-" + unique);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int insertBed(int room, String suffix) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return insertBed(connection, room, suffix);
        }
    }

    private static int insertAdmission(Connection connection) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO admissions (patient_id, bed_id, admission_date) VALUES (?, ?, ?) RETURNING admission_id")) {
            statement.setInt(1, patientId);
            statement.setInt(2, activeBedId);
            statement.setDate(3, Date.valueOf(LocalDate.now()));
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }

    private static int bedId(int room, String number) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT bed_id FROM beds WHERE room_id = ? AND bed_number = ?")) {
            statement.setInt(1, room);
            statement.setString(2, number);
            try (ResultSet result = statement.executeQuery()) { assertTrue(result.next()); return result.getInt(1); }
        }
    }
}
