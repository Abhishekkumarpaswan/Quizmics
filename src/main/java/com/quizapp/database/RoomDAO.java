package com.quizapp.database;

import com.quizapp.model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    /**
     * Retrieves all rooms from the database.
     *
     * @return A list of Room objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_name"),
                        rs.getInt("quiz_id"),
                        rs.getInt("created_by")
                ));
            }
        }
        return rooms;
    }

    /**
     * Creates a new room in the database.
     *
     * @param roomName  The name of the room.
     * @param quizId    The ID of the quiz associated with the room.
     * @param createdBy The ID of the user who created the room.
     * @return The ID of the newly created room, or -1 if the operation fails.
     * @throws SQLException If a database access error occurs.
     */
    public int createRoom(String roomName, int quizId, int createdBy) throws SQLException {
        String query = "INSERT INTO rooms (room_name, quiz_id, created_by) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, roomName);
            stmt.setInt(2, quizId);
            stmt.setInt(3, createdBy);
            stmt.executeUpdate();

            // Retrieve the generated room ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1; // Return -1 if the operation fails
    }

    /**
     * Retrieves a room by its ID.
     *
     * @param roomId The ID of the room.
     * @return The Room object, or null if the room is not found.
     * @throws SQLException If a database access error occurs.
     */
    public Room getRoomById(int roomId) throws SQLException {
        String query = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Room(
                            rs.getInt("room_id"),
                            rs.getString("room_name"),
                            rs.getInt("quiz_id"),
                            rs.getInt("created_by")
                    );
                }
            }
        }
        return null; // Return null if the room is not found
    }

    /**
     * Deletes a room by its ID.
     *
     * @param roomId The ID of the room to delete.
     * @return True if the room was deleted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteRoom(int roomId) throws SQLException {
        String query = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if the room was deleted
        }
    }
}