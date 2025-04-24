package com.quizapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {
    private static final String LOGIN_PREFIX = "LOGIN:";
    private static final String LOGIN_SUCCESS_PREFIX = "LOGIN_SUCCESS:";
    private static final String LOGIN_FAILED_PREFIX = "LOGIN_FAILED";
    private static final String CREATE_QUIZ_PREFIX = "CREATE_QUIZ:";
    private static final String QUIZ_CREATED_PREFIX = "QUIZ_CREATED:";
    private static final String CREATE_ROOM_PREFIX = "CREATE_ROOM:";
    private static final String ROOM_CREATED_PREFIX = "ROOM_CREATED:";
    private static final String GET_ACTIVE_ROOMS_PREFIX = "GET_ACTIVE_ROOMS";
    private static final String ACTIVE_ROOMS_PREFIX = "ACTIVE_ROOMS:";
    private static final String JOIN_ROOM_PREFIX = "JOIN_ROOM:";
    private static final String JOIN_ROOM_SUCCESS_PREFIX = "JOIN_ROOM:SUCCESS:";
    private static final String GET_QUIZ_DATA_PREFIX = "GET_QUIZ_DATA:";
    private static final String QUIZ_DATA_PREFIX = "QUIZ_DATA:";
    private static final String QUIZ_DATA_SUCCESS_PREFIX = "QUIZ_DATA:SUCCESS:";
    private static final String QUIZ_DATA_FAILED_PREFIX = "QUIZ_DATA:FAILED";
    private static final String GET_ROOM_INFO_PREFIX = "GET_ROOM_INFO:";
    private static final String ROOM_INFO_PREFIX = "ROOM_INFO:";
    private static final String QUIT_PREFIX = "QUIT";
    private static final String GET_PLAYER_LIST_PREFIX = "GET_PLAYER_LIST:";
    private static final String LOGOUT_PREFIX = "LOGOUT:";
    private static final String PLAYER_LIST_PREFIX = "PLAYER_LIST:";
    private static final String LEAVE_ROOM_PREFIX = "LEAVE_ROOM:";
    private static final String REGISTER_PREFIX = "REGISTER:";
    private static final String REGISTER_SUCCESS_PREFIX = "REGISTER_SUCCESS:";

    private Socket clientSocket;
    private Connection connection;
    private PrintWriter out;
    private BufferedReader in;
    private int userId; // User ID of the connected client
    private int roomId = -1; // Room ID of the client's current room
    private String username;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket, Connection connection) {
        this.clientSocket = socket; // Initialize client socket
        this.connection = connection; // Initialize database connection
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true); // Output stream for sending messages to client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Input stream for reading messages from client
            String inputLine;

            while ((inputLine = in.readLine()) != null) { // Read incoming messages from client
                // Handle client requests
                logger.info("Received request: " + inputLine);

                if (inputLine.startsWith(LOGIN_PREFIX)) {
                    String[] request = inputLine.substring(LOGIN_PREFIX.length()).split(":");
                    if (request.length == 2) {
                        handleLogin(request[0], request[1]); // Handle login request
                    } else {
                        out.println("ERROR: Invalid LOGIN request format.");
                    }
                } else if(inputLine.startsWith(REGISTER_PREFIX)){
                    String[] request = inputLine.substring(REGISTER_PREFIX.length()).split(":");
                    if (request.length == 2) {
                        handleRegister(request[0], request[1]); // Handle register request
                    } else {
                        out.println("ERROR: Invalid REGISTER request format.");
                    }
                }else if (inputLine.startsWith(LOGOUT_PREFIX)) {
                    handleLogout(inputLine.substring(LOGOUT_PREFIX.length()));
                } else if (inputLine.startsWith(CREATE_QUIZ_PREFIX)) {
                    String[] request = inputLine.substring(CREATE_QUIZ_PREFIX.length()).split(":");
                    if (request.length == 3) {
                        handleCreateQuiz(request[0], request[1], request[2]);
                    } else {
                        out.println("ERROR: Invalid CREATE_QUIZ request format.");
                    }
                } else if (inputLine.startsWith(GET_QUIZ_DATA_PREFIX)) {
                    handleGetQuizData(inputLine.substring(GET_QUIZ_DATA_PREFIX.length()));
                } else if (inputLine.startsWith(CREATE_ROOM_PREFIX)) {
                    String[] request = inputLine.substring(CREATE_ROOM_PREFIX.length()).split(":");
                    if (request.length == 3) {
                        handleCreateRoom(request[0], request[1], request[2]);
                    } else {
                        out.println("ERROR: Invalid CREATE_ROOM request format.");
                    }
                } else if (inputLine.startsWith(GET_ACTIVE_ROOMS_PREFIX)) {
                    handleGetActiveRooms();
                } else if (inputLine.startsWith(JOIN_ROOM_PREFIX)) {
                    String[] request = inputLine.substring(JOIN_ROOM_PREFIX.length()).split(":");
                    if (request.length == 2) {
                        handleJoinRoom(request[0], request[1]);
                    } else {
                        out.println("ERROR: Invalid JOIN_ROOM request format.");
                    }
                } else if (inputLine.startsWith(GET_PLAYER_LIST_PREFIX)) {
                    handleGetPlayerList(inputLine.substring(GET_PLAYER_LIST_PREFIX.length()));
                } else if (inputLine.startsWith(GET_ROOM_INFO_PREFIX)) {
                    handleGetRoomInfo(inputLine.substring(GET_ROOM_INFO_PREFIX.length()));
                } else if (inputLine.startsWith(LEAVE_ROOM_PREFIX)) {
                    handleLeaveRoom(inputLine.substring(LEAVE_ROOM_PREFIX.length()));
                } else if (inputLine.startsWith(QUIT_PREFIX)) {
                    break;
                } else {
                    out.println("ERROR: Unknown request.");
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error handling client connection", e);
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing client connection", e);
            }
        }
    }

    private void handleLogin(String username, String password) {
        try {
            String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("user_id");
                this.username = username;
                out.println(LOGIN_SUCCESS_PREFIX + userId);
            } else {
                out.println(LOGIN_FAILED_PREFIX);
            }
        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void handleRegister(String username, String password) {
        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();

            out.println(REGISTER_SUCCESS_PREFIX + username);
        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }
    private void handleLogout(String username) {
        out.println("LOGOUT_SUCCESS");
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing client socket", e);
        }
    }

    private void handleCreateQuiz(String quizName, String userId, String questions) {
        try {
            String query = "INSERT INTO quizzes (quiz_name, created_by) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, quizName);
            stmt.setInt(2, Integer.parseInt(userId));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int quizId = rs.getInt(1);
                String[] questionArray = questions.split(";");
                for (String question : questionArray) {
                    String[] parts = question.split("\\|");
                    String questionText = parts[0];
                    String option1 = parts[1];
                    String option2 = parts[2];
                    String option3 = parts[3];
                    String option4 = parts[4];
                    String correctAnswer = parts[5];

                    String questionQuery = "INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement questionStmt = connection.prepareStatement(questionQuery);
                    questionStmt.setInt(1, quizId);
                    questionStmt.setString(2, questionText);
                    questionStmt.setString(3, option1);
                    questionStmt.setString(4, option2);
                    questionStmt.setString(5, option3);
                    questionStmt.setString(6, option4);
                    questionStmt.setString(7, correctAnswer);
                    questionStmt.executeUpdate();
                }
                out.println(QUIZ_CREATED_PREFIX + quizId);
            } else {
                out.println("ERROR: Failed to create quiz.");
            }
        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void handleGetQuizData(String roomId) {
        try {
            String query = "SELECT q.question_text, q.option1, q.option2, q.option3, q.option4, q.correct_answer FROM questions q JOIN rooms r ON q.quiz_id = r.quiz_id WHERE r.room_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(roomId));
            ResultSet rs = stmt.executeQuery();

            StringBuilder quizData = new StringBuilder(QUIZ_DATA_SUCCESS_PREFIX);
            while (rs.next()) {
                quizData.append(rs.getString("question_text")).append("|")
                        .append(rs.getString("option1")).append("|")
                        .append(rs.getString("option2")).append("|")
                        .append(rs.getString("option3")).append("|")
                        .append(rs.getString("option4")).append("|")
                        .append(rs.getString("correct_answer")).append(";");
            }
            out.println(quizData.toString());
        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void handleCreateRoom(String roomName, String quizId, String userId) {
        try {
            String query = "INSERT INTO rooms (room_name, quiz_id, created_by) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, roomName);
            stmt.setInt(2, Integer.parseInt(quizId));
            stmt.setInt(3, Integer.parseInt(userId));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int roomId = rs.getInt(1);
                RoomManager roomManager = new RoomManager(roomId);
                QuizServer.addRoomManager(roomId, roomManager);
                out.println(ROOM_CREATED_PREFIX + roomId);
            } else {
                out.println("ERROR: Failed to create room.");
            }
        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void handleGetActiveRooms() {
        out.println(QuizServer.getActiveRooms());
    }

    private void handleJoinRoom(String roomId, String userId) {
        RoomManager roomManager = QuizServer.getRoomManager(Integer.parseInt(roomId));
        if (roomManager != null) {
            roomManager.addClient(this);
            out.println(JOIN_ROOM_SUCCESS_PREFIX + roomId);
            roomManager.broadcastPlayerList();
        } else {
            out.println("ERROR: Room not found.");
        }
    }

    private void handleGetPlayerList(String roomId) {
        RoomManager roomManager = QuizServer.getRoomManager(Integer.parseInt(roomId));
        if (roomManager != null) {
            roomManager.broadcastPlayerList();
        } else {
            out.println("ERROR: Room not found.");
        }
    }

    private void handleGetRoomInfo(String roomId) {
        try {
            String query = "SELECT r.room_name, q.quiz_name FROM rooms r JOIN quizzes q ON r.quiz_id = q.quiz_id WHERE r.room_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(roomId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String roomName = rs.getString("room_name");
                String quizName = rs.getString("quiz_name");
                out.println(ROOM_INFO_PREFIX + roomName + "," + quizName);
            } else {
                out.println("ERROR: Room not found.");
            }
        } catch (SQLException e) {
            out.println("ERROR: " + e.getMessage());
        }
    }

    private void handleLeaveRoom(String roomIdAndUserId) {
        try {
            String[] parts = roomIdAndUserId.split(":");
            int roomId = Integer.parseInt(parts[0]);
            int userId = Integer.parseInt(parts[1]);
            RoomManager roomManager = QuizServer.getRoomManager(roomId);
            if (roomManager != null) {
                roomManager.removeClient(this);
                out.println("LEAVE_ROOM_SUCCESS");
            } else {
                out.println("ERROR: Room not found.");
            }
        } catch (NumberFormatException e) {
            out.println("ERROR: Invalid room ID or user ID format.");
        }
    }
}
