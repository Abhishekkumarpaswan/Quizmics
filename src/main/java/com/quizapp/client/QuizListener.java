package com.quizapp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class QuizListener implements ActionListener {

    private QuizGUI quizGUI;
    private PrintWriter out;
    private BufferedReader in;
    private int userId;
    private int roomId;
    private int quizId;

    public QuizListener(QuizGUI quizGUI, PrintWriter out, BufferedReader in) {
        this.quizGUI = quizGUI;
        this.out = out;
        this.in = in;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quizGUI.getLoginButton()) {
            handleLogin();
        } else if (e.getSource() == quizGUI.getCreateQuizButton()) {
            handleCreateQuiz();
        } else if (e.getSource() == quizGUI.getCreateRoomButton()) {
            handleCreateRoom();
        } else if (e.getSource() == quizGUI.getJoinRoomButton()) {
            handleJoinRoom();
        } else if (e.getSource() == quizGUI.getStartQuizButton()) {
            handleStartQuiz(String.valueOf(quizId));
        }
    }

    private void handleLogin() {
        String username = quizGUI.getUsernameField().getText();
        String password = new String(quizGUI.getPasswordField().getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username and password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("LOGIN:" + username + ":" + password);
        try {
            String response = in.readLine();
            if (response.startsWith("LOGIN_SUCCESS")) {
                userId = Integer.parseInt(response.split(":")[1]);
                JOptionPane.showMessageDialog(null, "Login successful!");
                quizGUI.enableQuizFeatures(); // Enable quiz-related features after login
            } else {
                JOptionPane.showMessageDialog(null, "Login failed! Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateQuiz() {
        String quizName = JOptionPane.showInputDialog("Enter quiz name:");
        if (quizName == null || quizName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Quiz name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("CREATE_QUIZ:" + quizName + ":" + userId);
        try {
            String response = in.readLine();
            if (response.startsWith("QUIZ_CREATED")) {
                quizId = Integer.parseInt(response.split(":")[1]); // Store the created quiz ID
                JOptionPane.showMessageDialog(null, "Quiz created successfully! Quiz ID: " + quizId);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create quiz: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateRoom() {
        String roomName = JOptionPane.showInputDialog("Enter room name:");
        if (roomName == null || roomName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Room name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String quizIdInput = JOptionPane.showInputDialog("Enter quiz ID:");
        if (quizIdInput == null || quizIdInput.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Quiz ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("CREATE_ROOM:" + roomName + ":" + quizIdInput + ":" + userId); // Send quizId and userId
        try {
            String response = in.readLine();
            if (response.startsWith("ROOM_CREATED")) {
                roomId = Integer.parseInt(response.split(":")[1]);
                this.quizId = Integer.parseInt(quizIdInput); // Make sure quizId is set here as well.
                JOptionPane.showMessageDialog(null, "Room created successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create room: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleJoinRoom() {
        String roomIdInput = JOptionPane.showInputDialog("Enter room ID:");
        if (roomIdInput == null || roomIdInput.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Room ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("JOIN_ROOM:" + roomIdInput + ":" + userId);
        try {
            String response = in.readLine();
            if (response.startsWith("JOIN_ROOM:CLIENT_JOINED")) {
                this.roomId = Integer.parseInt(roomIdInput);

                // Request the quizId from the server
                out.println("GET_QUIZ_ID:" + roomIdInput);
                String quizIdResponse = in.readLine();

                if (quizIdResponse.startsWith("QUIZ_ID:")) {
                    this.quizId = Integer.parseInt(quizIdResponse.split(":")[1]);
                    JOptionPane.showMessageDialog(null, "Joined room successfully! Quiz ID: " + quizId);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to get quiz ID: " + quizIdResponse, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (response.startsWith("JOIN_ROOM:ERROR")) {
                String errorMessage = response.split(":")[2];
                JOptionPane.showMessageDialog(null, "Failed to join room: " + errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Unexpected response: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleStartQuiz(String quizId) {
        out.println("START_QUIZ:" + quizId);
        try {
            String response = in.readLine();
            if (response != null && response.startsWith("START_QUIZ:QUESTIONS")) {
                String questions = response.substring("START_QUIZ:QUESTIONS:".length()); // Extract the questions
                quizGUI.getQuizArea().setText(questions);
            } else if (response != null && response.startsWith("START_QUIZ:ERROR")) {
                String errorMessage = response.split(":")[2];
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Unexpected response: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
