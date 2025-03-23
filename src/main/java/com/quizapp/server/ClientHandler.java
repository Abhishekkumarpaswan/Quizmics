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
    private int userId; // User ID of the connected client
    private int roomId; // Room ID of the client's current room

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket, Connection connection) {
        this.clientSocket = socket; // Initialize client socket
        this.connection = connection; // Initialize database connection
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true); // Output stream for sending messages to client
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Input stream for reading messages from client
            String inputLine;

            while ((inputLine = in.readLine()) != null) { // Read incoming messages from client
                // Handle client requests
                String[] request = inputLine.split(":"); // Split request into parts based on ':'
                logger.info("Received request: " + inputLine);

                switch (request[0]) { // Determine action based on request type
                    case "LOGIN":
                        handleLogin(request[1], request[2]); // Handle login request
                        break;
                    case "CREATE_QUIZ":
                        if (request.length == 4) { // Ensure all required arguments are provided
                            handleCreateQuiz(request[1], request[2], request[3]); // Handle create quiz request
                        } else {
                            out.println("ERROR: Insufficient arguments for CREATE_QUIZ."); // Error message for insufficient arguments
                        }
                        break;
                    case "GET_QUIZZES":
                        handleGetQuizzes(); // Handle get quizzes request
                        break;
                    case "CREATE_ROOM":
                        if (request.length == 4) { // Ensure all required arguments are provided
                            handleCreateRoom(request[1], request[2], request[3]); // Handle create room request
                        } else {
                            out.println("ERROR: Insufficient arguments for CREATE_ROOM."); // Error message for insufficient arguments
                        }
                        break;
                    case "JOIN_ROOM":
                        handleJoinRoom(request[1], request[2]); // Handle join room request
                        break;
                    case "START_QUIZ":
                        handleStartQuiz(request[1]); // Handle start quiz request
                        break;
                    case "SUBMIT_ANSWER":
                        handleSubmitAnswer(request[1], request[2], request[3]); // Handle submit answer request
                        break;
                    case "GET_QUIZ_ID": // New case to handle request for quiz ID
                        handleGetQuizId(request[1]); // Handle get quiz ID request
                        break;
                    default:
                        out.println("ERROR: Invalid request."); // Error message for invalid requests
                        break;
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error in ClientHandler: ", e);
        } finally {
            try {
                if (clientSocket != null) clientSocket.close();
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing resources: ", e);
            }
        }
    }

    private void handleLogin(String username, String password) {
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
            logger.log(Level.SEVERE, "SQL error during login: ", e);
        }
    }

    private void handleCreateQuiz(String quizName, String userIdStr, String questionsStr) {
        String query = "INSERT INTO quizzes (quiz_name, created_by) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quizName);
            stmt.setInt(2, Integer.parseInt(userIdStr));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int createdQuizId = rs.getInt(1);
                    out.println("QUIZ_CREATED:" + createdQuizId);

                    insertQuestions(questionsStr, createdQuizId);
                }
            }
        } catch (SQLException e) {
            out.println("ERROR: Failed to create quiz.");
            logger.log(Level.SEVERE, "SQL error during quiz creation: ", e);
        }
    }

    private void insertQuestions(String questionsStr, int quizId) {
        String[] questionArray = questionsStr.split(";");
        String query = "INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (String questionData : questionArray) {
                if (!questionData.isEmpty()) {
                    String[] parts = questionData.split(",");

                    stmt.setInt(1, quizId);
                    stmt.setString(2, parts[0]);
                    stmt.setString(3, parts[1]);
                    stmt.setString(4, parts[2]);
                    stmt.setString(5, parts[3]);
                    stmt.setString(6, parts[4]);
                    stmt.setString(7, parts[5]);

                    stmt.addBatch();
                }
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "ERROR: Failed to insert questions.", e);
        }
    }

    private void handleGetQuizzes() {
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
            logger.log(Level.SEVERE, "SQL error during fetching quizzes: ", e);
        }
    }

    private void handleCreateRoom(String roomName, String quizIdStr, String userIdStr) {
        String query = "INSERT INTO rooms (room_name, quiz_id, created_by) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, roomName);
            stmt.setInt(2, Integer.parseInt(quizIdStr));
            stmt.setInt(3, Integer.parseInt(userIdStr));

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
            logger.log(Level.SEVERE,"SQL error during room creation: ",e);
        }
    }

    private void handleJoinRoom(String roomIdStr, String userIdStr) {
        try {
            this.roomId = Integer.parseInt(roomIdStr);
            this.userId = Integer.parseInt(userIdStr);

            RoomManager roomManager = QuizServer.getRoomManager(this.roomId);

            if (roomManager != null) {
                roomManager.addClient(this);

                out.println("JOIN_ROOM:CLIENT_JOINED:" + userId);

            } else {
                out.println("JOIN_ROOM:ERROR:ROOM_NOT_FOUND");
            }
        } catch (NumberFormatException e) {
            out.println("JOIN_ROOM:ERROR:Invalid room ID format. Room ID must be numeric.");
            logger.log(Level.WARNING, "Invalid room ID format: " + roomIdStr, e);
        }
    }

    private void handleStartQuiz(String quizIdStr) {
        String query = "SELECT * FROM questions WHERE quiz_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1,Integer.parseInt(quizIdStr));

            try(ResultSet rs=stmt.executeQuery()) {
                StringBuilder questions=new StringBuilder();

                while(rs.next()) {
                    questions.append(rs.getString("question_text")).append(",")
                            .append(rs.getString("option1")).append(",")
                            .append(rs.getString("option2")).append(",")
                            .append(rs.getString("option3")).append(",")
                            .append(rs.getString("option4")).append(",")
                            .append(rs.getString("correct_answer")).append(";");
                }

                if(questions.length()==0){
                    out.println("START_QUIZ:ERROR:No questions found for quiz ID "+quizIdStr);
                } else {
                    out.println("START_QUIZ:QUESTIONS:"+questions.toString());

                    RoomManager roomManager=QuizServer.getRoomManager(roomId);

                    if(roomManager!=null){
                        roomManager.broadcast("START_QUIZ:QUESTIONS:"+questions.toString());

                    } else {
                        out.println("START_QUIZ:ERROR:ROOM_NOT_FOUND");

                    }

                }

            }

        } catch(SQLException e){
            out.println("ERROR: Failed to fetch quiz questions.");
            logger.log(Level.SEVERE,"Failed to fetch quiz questions:"+e.getMessage());
        }
    }

    private void handleSubmitAnswer(String userIDStr,String quizIDStr,String scoreStr){
        String query="INSERT INTO results(user_id,quiz_id,score) VALUES(?,?,?)";
        try(PreparedStatement stmt=connection.prepareStatement(query)){
            stmt.setInt(1,Integer.parseInt(userIDStr));
            stmt.setInt(2,Integer.parseInt(quizIDStr));
            stmt.setInt(3,Integer.parseInt(scoreStr));
            stmt.executeUpdate();
            out.println("RESULT_SAVED");
        } catch(SQLException e){
            out.println("ERROR: Failed to submit answer.");
            logger.log(Level.SEVERE,"Failed to submit answer:"+e.getMessage());
        }
    }

    public void sendMessage(String message){
        try{
            out.println(message);
            out.flush();
        } catch(Exception e){
            System.err.println("Failed to send message to client "+userId+": "+e.getMessage());
        }
    }

    public int getUserId(){ return this.userId; }

    private void handleGetQuizId(String roomIDStr){
        try{
            int roomID=Integer.parseInt(roomIDStr);
            String query="SELECT quiz_id FROM rooms WHERE room_id=?";

            try(PreparedStatement stmt=connection.prepareStatement(query)){
                stmt.setInt(1,roomID);
                ResultSet rs=stmt.executeQuery();

                if(rs.next()){
                    int quizID=rs.getInt("quiz_id");
                    out.println("QUIZ_ID:"+quizID);
                }else{
                    out.println("QUIZ_ID:ERROR:Room not found");
                }
            }catch(SQLException e){
                out.println("QUIZ_ID:ERROR:Database error");
                logger.log(Level.SEVERE,"Database error while fetching Quiz ID:"+e.getMessage());
            }
        }catch(NumberFormatException e){
            out.println("QUIZ_ID:ERROR:Invalid room ID format");
        }
    }
}
