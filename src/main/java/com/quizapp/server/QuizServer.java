package com.quizapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class QuizServer {
    private static final String ACTIVE_ROOMS_PREFIX = "ACTIVE_ROOMS:";
    private static final Logger logger = Logger.getLogger(RoomManager.class.getName());
    private static final int PORT = 12345; // Port for the server
    private static Connection connection; // Database connection
    private static final Map<Integer, RoomManager> rooms = new HashMap<>(); // Room ID -> RoomManager
    public static void main(String[] args) {
        try {
            // Connect to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_db", "root", "ABHIsql@1");
            System.out.println("Connected to the database.");

            // Start the server
            startServer();
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the server and listens for incoming client connections.
     */
    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Changed to use the constant PORT
            System.out.println("Server started on port " + PORT);
            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread to handle the client
                ClientHandler clientHandler = new ClientHandler(clientSocket, connection);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }));
    }

    /**
     * Retrieves the RoomManager for a specific room.
     *
     * @param roomId The ID of the room.
     * @return The RoomManager instance, or null if the room does not exist.
     */
    public static synchronized RoomManager getRoomManager(int roomId) {
        return rooms.get(roomId);
    }

    /**
     * Adds a new RoomManager to the rooms map.
     *
     * @param roomId The ID of the room.
     * @param roomManager The RoomManager instance for the room.
     */
    public static synchronized void addRoomManager(int roomId, RoomManager roomManager) {
        rooms.put(roomId, roomManager);
        System.out.println("Active Room " + roomId + " created and added to the server.");
    }

    /**
     * Removes a RoomManager from the rooms map.
     *
     * @param roomId The ID of the room to remove.
     */
    public static synchronized void removeRoomManager(int roomId) {
        rooms.remove(roomId);
        System.out.println("Room " + roomId + " removed from the server.");
    }

    public static synchronized String getActiveRooms() {
        if (rooms.isEmpty()) {
            return ACTIVE_ROOMS_PREFIX + "EMPTY";
        }

        String roomIds = rooms.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        if (roomIds.isEmpty()) {
            return ACTIVE_ROOMS_PREFIX + "EMPTY";
        }

        String query = "SELECT r.room_id, r.room_name, q.quiz_name " +
                "FROM rooms r JOIN quizzes q ON r.quiz_id = q.quiz_id " +
                "WHERE r.room_id IN (" + roomIds + ")";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            StringBuilder response = new StringBuilder(ACTIVE_ROOMS_PREFIX);
            while (rs.next()) {
                response.append(rs.getInt("room_id")).append(",")
                        .append(rs.getString("room_name")).append(",")
                        .append(rs.getString("quiz_name")).append(";");
            }

            return response.toString();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting active rooms", e);
            return ACTIVE_ROOMS_PREFIX + "EMPTY";
        }
    }

    public static synchronized String getActiveRoomIds() {
        if (rooms.isEmpty()) {
            return "";
        }

        // Create comma-separated list of room IDs
        return rooms.keySet().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
