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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends Thread {

    private Socket clientSocket;
    private Connection connection;
    private PrintWriter out;
    private BufferedReader in;
    private int userId;
    private int roomId;

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket, Connection connection) {
        this.clientSocket = socket;
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                // Handle client requests
                String[] request = inputLine.split(":");
                logger.info("Received request: " + inputLine);

                switch (request[0]) {
                    case "LOGIN":
                        handleLogin(request[1], request[2]);
                        break;
                    case "CREATE_QUIZ":
                        handleCreateQuiz(request[1], request[2]);
                        break;
                    case "GET_QUIZZES":
                        handleGetQuizzes();
                        break;
                    case "CREATE_ROOM":
                        handleCreateRoom(request[1], request[2], request[3]);
                        break;
                    case "JOIN_ROOM":
                        handleJoinRoom(request[1], request[2]);
                        break;
                    case "START_QUIZ":
                        handleStartQuiz(request[1]);
                        break;
                    case "SUBMIT_ANSWER":
                        handleSubmitAnswer(request[1], request[2], request[3]);
                        break;
                    case "GET_QUIZ_ID": // New case to handle request for quiz ID
                        handleGetQuizId(request[1]);
                        break;
                    default:
                        out.println("ERROR: Invalid request.");
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleLogin(String username, String password) {
        // Validate user login
        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("user_id");
                    out.println("LOGIN_SUCCESS:" + userId);
                } else {
                    out.println("LOGIN_FAILED");
                }
            }
        } catch (SQLException e) {
            out.println("ERROR: Failed to process login.");
            e.printStackTrace();
        }
    }

    private void handleCreateQuiz(String quizName, String userId) {
        // Create a new quiz
        String query = "INSERT INTO quizzes (quiz_name, created_by) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quizName);
            stmt.setInt(2, Integer.parseInt(userId));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    out.println("QUIZ_CREATED:" + rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            out.println("ERROR: Failed to create quiz.");
            e.printStackTrace();
        }
    }

    private void handleGetQuizzes() {
        // Fetch all quizzes
        String query = "SELECT quiz_id, quiz_name FROM quizzes";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            StringBuilder quizzes = new StringBuilder();
            while (rs.next()) {
                quizzes.append(rs.getInt("quiz_id")).append(",").append(rs.getString("quiz_name")).append(";");
            }

            out.println("QUIZZES:" + quizzes.toString());

        } catch (SQLException e) {
            out.println("ERROR: Failed to fetch quizzes.");
            e.printStackTrace();
        }
    }

    private void handleCreateRoom(String roomName, String quizId, String userId) {
        String query = "INSERT INTO rooms (room_name, quiz_id, created_by) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, roomName);
            stmt.setInt(2, Integer.parseInt(quizId));
            stmt.setInt(3, Integer.parseInt(userId));
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int roomId = rs.getInt(1);
                    RoomManager roomManager = new RoomManager(roomId);
                    QuizServer.addRoomManager(roomId, roomManager);
                    out.println("ROOM_CREATED:" + roomId);
                }
            }
        } catch (SQLException e) {
            out.println("ERROR: Failed to create room.");
            e.printStackTrace();
        }
    }

    private void handleJoinRoom(String roomId, String userId) {
        this.roomId = Integer.parseInt(roomId);
        this.userId = Integer.parseInt(userId);
        RoomManager roomManager = QuizServer.getRoomManager(this.roomId);
        if (roomManager != null) {
            roomManager.addClient(this);
            out.println("JOIN_ROOM:CLIENT_JOINED:" + userId);
        } else {
            out.println("JOIN_ROOM:ERROR:ROOM_NOT_FOUND");
        }
    }

    private void handleStartQuiz(String quizId) {
        // Fetch questions for the quiz
        String query = "SELECT * FROM questions WHERE quiz_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(quizId)); // Set the parameter before executing the query
            try (ResultSet rs = stmt.executeQuery()) {
                StringBuilder questions = new StringBuilder();
                while (rs.next()) {
                    questions.append(rs.getString("question_text")).append(",")
                            .append(rs.getString("option1")).append(",")
                            .append(rs.getString("option2")).append(",")
                            .append(rs.getString("option3")).append(",")
                            .append(rs.getString("option4")).append(",")
                            .append(rs.getString("correct_answer")).append(";");
                }

                if (questions.length() == 0) {
                    out.println("START_QUIZ:ERROR:No questions found for quiz ID " + quizId);
                } else {
                    out.println("START_QUIZ:QUESTIONS:" + questions.toString()); // Append questions to the response

                    // Broadcast the questions to all clients in the room
                    RoomManager roomManager = QuizServer.getRoomManager(roomId);
                    if (roomManager != null) {
                        roomManager.broadcast("START_QUIZ:QUESTIONS:" + questions.toString());
                    } else {
                        out.println("START_QUIZ:ERROR:ROOM_NOT_FOUND"); // Notify the client if the room doesn't exist
                    }
                }
            }
        } catch (SQLException e) {
            out.println("ERROR: Failed to fetch quiz questions."); // Notify the client of the error
            logger.log(Level.SEVERE, "Failed to fetch quiz qusetions:"+e.getMessage());
        }
    }

    private void handleSubmitAnswer(String userId, String quizId, String score) {
        // Save the quiz result
        String query = "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setInt(2, Integer.parseInt(quizId));
            stmt.setInt(3, Integer.parseInt(score));
            stmt.executeUpdate();
            out.println("RESULT_SAVED");
        } catch (SQLException e) {
            out.println("ERROR: Failed to submit answer.");
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.println(message);
            out.flush(); // Ensure the message is sent immediately
        } catch (Exception e) {
            System.err.println("Failed to send message to client " + userId + ": " + e.getMessage());
            // Optionally, notify the server or take other actions
        }
    }

    // Add getUserId method
    public int getUserId() {
        return this.userId;
    }

    //New Method to Handle Get Quiz ID request
    private void handleGetQuizId(String roomIdStr) {
        try {
            int roomId = Integer.parseInt(roomIdStr);
            String query = "SELECT quiz_id FROM rooms WHERE room_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int quizId = rs.getInt("quiz_id");
                    out.println("QUIZ_ID:" + quizId);  // Send the quiz ID back to the client
                } else {
                    out.println("QUIZ_ID:ERROR:Room not found");
                }
            } catch (SQLException e) {
                out.println("QUIZ_ID:ERROR:Database error");
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            out.println("QUIZ_ID:ERROR:Invalid room ID format");
        }
    }
}
