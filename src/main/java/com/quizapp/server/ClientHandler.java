package com.quizapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private Connection connection;
    private PrintWriter out;
    private BufferedReader in;
    private int userId;
    private int roomId;

    public ClientHandler(Socket socket, Connection connection) {
        this.clientSocket = socket;
        this.connection = connection;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Handle client requests
                String[] request = inputLine.split(":");
                switch (request[0]) {
                    case "LOGIN":
                        try {
                            handleLogin(request[1], request[2]);
                        } catch (SQLException e) {
                            out.println("ERROR: Failed to process login.");
                            e.printStackTrace();
                        }
                        break;
                    case "CREATE_QUIZ":
                        try {
                            handleCreateQuiz(request[1], request[2]);
                        } catch (SQLException e) {
                            out.println("ERROR: Failed to create quiz.");
                            e.printStackTrace();
                        }
                        break;
                    case "GET_QUIZZES":
                        try {
                            handleGetQuizzes();
                        } catch (SQLException e) {
                            out.println("ERROR: Failed to fetch quizzes.");
                            e.printStackTrace();
                        }
                        break;
                    case "CREATE_ROOM":
                        try {
                            handleCreateRoom(request[1], request[2]);
                        } catch (SQLException e) {
                            out.println("ERROR: Failed to create room.");
                            e.printStackTrace();
                        }
                        break;
                    case "JOIN_ROOM":
                        handleJoinRoom(request[1], request[2]);
                        break;
                    case "START_QUIZ":
                        try {
                            handleStartQuiz(request[1]);
                        } catch (SQLException e) {
                            out.println("ERROR: Failed to start quiz.");
                            e.printStackTrace();
                        }
                        break;
                    case "SUBMIT_ANSWER":
                        try {
                            handleSubmitAnswer(request[1], request[2], request[3]);
                        } catch (SQLException e) {
                            out.println("ERROR: Failed to submit answer.");
                            e.printStackTrace();
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(String username, String password) throws SQLException {
        // Validate user login
        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            userId = rs.getInt("user_id");
            out.println("LOGIN_SUCCESS:" + userId);
        } else {
            out.println("LOGIN_FAILED");
        }
    }

    private void handleCreateQuiz(String quizName, String userId) throws SQLException {
        // Ensure userId is a valid integer
        int createdBy = Integer.parseInt(userId);

        // Create a new quiz
        String query = "INSERT INTO quizzes (quiz_name, created_by) VALUES (?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, quizName);
        stmt.setInt(2, createdBy);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            out.println("QUIZ_CREATED:" + rs.getInt(1));
        }
    }

    private void handleGetQuizzes() throws SQLException {
        // Fetch all quizzes
        String query = "SELECT quiz_id, quiz_name FROM quizzes";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        StringBuilder quizzes = new StringBuilder();
        while (rs.next()) {
            quizzes.append(rs.getInt("quiz_id")).append(",").append(rs.getString("quiz_name")).append(";");
        }
        out.println("QUIZZES:" + quizzes.toString());
    }

    private void handleCreateRoom(String roomName, String quizId) throws SQLException {
        // Create a new room
        String query = "INSERT INTO rooms (room_name, quiz_id, created_by) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, roomName);
        stmt.setInt(2, Integer.parseInt(quizId));
        stmt.setInt(3, userId);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        if (rs.next()) {
            int roomId = rs.getInt(1);
            RoomManager roomManager = new RoomManager(roomId);
            QuizServer.addRoomManager(roomId, roomManager);
            out.println("ROOM_CREATED:" + roomId);
        }
    }

    private void handleJoinRoom(String roomId, String userId) {
        this.roomId = Integer.parseInt(roomId);
        RoomManager roomManager = QuizServer.getRoomManager(this.roomId);
        if (roomManager != null) {
            roomManager.addClient(this);
            out.println("JOINED_ROOM:" + roomId);
        } else {
            out.println("ROOM_NOT_FOUND");
        }
    }

    private void handleStartQuiz(String quizId) throws SQLException {
        // Fetch questions for the quiz
        String query = "SELECT * FROM questions WHERE quiz_id = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, Integer.parseInt(quizId));
        ResultSet rs = stmt.executeQuery();

        StringBuilder questions = new StringBuilder();
        while (rs.next()) {
            questions.append(rs.getString("question_text")).append(",")
                    .append(rs.getString("option1")).append(",")
                    .append(rs.getString("option2")).append(",")
                    .append(rs.getString("option3")).append(",")
                    .append(rs.getString("option4")).append(",")
                    .append(rs.getString("correct_answer")).append(";");
        }
        RoomManager roomManager = QuizServer.getRoomManager(roomId);
        if (roomManager != null) {
            roomManager.broadcast("QUESTIONS:" + questions.toString());
        }
    }

    private void handleSubmitAnswer(String userId, String quizId, String score) throws SQLException {
        // Save the quiz result
        String query = "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, Integer.parseInt(userId));
        stmt.setInt(2, Integer.parseInt(quizId));
        stmt.setInt(3, Integer.parseInt(score));
        stmt.executeUpdate();
        out.println("RESULT_SAVED");
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}