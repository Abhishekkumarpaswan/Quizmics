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

        StringBuilder questionsBuilder = new StringBuilder();

        while (true) {
            String question = JOptionPane.showInputDialog("Enter a question (or type 'done' to finish):");

            if (question == null || question.equalsIgnoreCase("done")) break;

            String option1 = JOptionPane.showInputDialog("Enter option 1:");
            String option2 = JOptionPane.showInputDialog("Enter option 2:");
            String option3 = JOptionPane.showInputDialog("Enter option 3:");
            String option4 = JOptionPane.showInputDialog("Enter option 4:");

            String correctAnswer = JOptionPane.showInputDialog("Enter the correct answer (option 1, 2, 3, or 4):");

            questionsBuilder.append(question).append(",")
                    .append(option1).append(",")
                    .append(option2).append(",")
                    .append(option3).append(",")
                    .append(option4).append(",")
                    .append(correctAnswer).append(";"); // Separate each question with a semicolon
        }

        out.println("CREATE_QUIZ:" + quizName + ":" + userId + ":" + questionsBuilder.toString());

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
        String quizIdInput = JOptionPane.showInputDialog("Enter quiz ID for this room:");
        if (quizIdInput == null || quizIdInput.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Quiz ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int quizId = Integer.parseInt(quizIdInput);
            out.println("CREATE_ROOM:" + roomName + ":" + quizId + ":" + userId); // Send room name, quiz ID, and user ID

            try {
                String response = in.readLine();

                if (response.startsWith("ROOM_CREATED")) {
                    roomId = Integer.parseInt(response.split(":")[1]);
                    JOptionPane.showMessageDialog(null, "Room created successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create room: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Quiz ID must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleJoinRoom() {
        String roomIdInput = JOptionPane.showInputDialog("Enter room ID:");

        if (roomIdInput == null || roomIdInput.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Room ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("JOIN_ROOM:" + roomIdInput + ":" + userId); // Send room ID and user ID

        try {
            String response = in.readLine();

            if (response!=null && response.startsWith("JOIN_ROOM:CLIENT_JOINED")) {
                this.roomId = Integer.parseInt(roomIdInput);

                out.println("GET_QUIZ_ID:" + roomIdInput); // Request the associated quiz ID

                String quizResponse = in.readLine();

                if (quizResponse!=null && quizResponse.startsWith("QUIZ_ID:")) {
                    this.quizId = Integer.parseInt(quizResponse.split(":")[1]);
                    JOptionPane.showMessageDialog(null, "Joined room successfully! Quiz ID: " + quizId);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to retrieve Quiz ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } else if (response!=null && response.startsWith("JOIN_ROOM:ERROR")) {
                String errorMessage = response.split(":")[2];
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Unexpected response from server.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleStartQuiz(String quizId) {
        out.println("START_QUIZ:" + quizId); // Send the Quiz ID

        try {
            String response = in.readLine();

            if (response.startsWith("START_QUIZ:QUESTIONS")) {
                String questions = response.substring("START_QUIZ:QUESTIONS:".length());
                quizGUI.getQuizArea().setText(questions); // Display questions in the GUI

            } else if (response.startsWith("START_QUIZ:ERROR")) {
                String errorMessage = response.split(":")[2];
                JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(null, "Unexpected response from server.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}