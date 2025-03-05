// Handles room-related database operations

package com.quizapp.database;

import com.quizapp.model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(rs.getInt("room_id"), rs.getString("room_name"), rs.getInt("quiz_id"), rs.getInt("created_by")));
            }
        }
        return rooms;
    }

    public int createRoom(String roomName, int quizId, int createdBy) throws SQLException {
        String query = "INSERT INTO rooms (room_name, quiz_id, created_by) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, roomName);
            stmt.setInt(2, quizId);
            stmt.setInt(3, createdBy);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
}