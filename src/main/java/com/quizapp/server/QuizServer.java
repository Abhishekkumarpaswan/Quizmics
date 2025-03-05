package com.quizapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QuizServer {
    private static final int PORT = 12345;
    private static Connection connection;
    private static Map<Integer, RoomManager> rooms = new HashMap<>(); // Room ID -> RoomManager

    public static void main(String[] args) {
        try {
            // Connect to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quiz_db", "root", "password");

            // Start the server
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                new ClientHandler(clientSocket, connection).start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static RoomManager getRoomManager(int roomId) {
        return rooms.get(roomId);
    }

    public static void addRoomManager(int roomId, RoomManager roomManager) {
        rooms.put(roomId, roomManager);
    }

    public static void removeRoomManager(int roomId) {
        rooms.remove(roomId);
    }
}