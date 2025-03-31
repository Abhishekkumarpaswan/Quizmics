package com.quizapp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import javax.swing.*;

public class QuizListener implements ActionListener {
    private static final long JOIN_TIMEOUT = 5000;
    private static final String CREATE_QUIZ_PREFIX = "CREATE_QUIZ:";
    private static final String QUIZ_CREATED_PREFIX = "QUIZ_CREATED:";
    private static final String CREATE_ROOM_PREFIX = "CREATE_ROOM:";
    private static final String ROOM_CREATED_PREFIX = "ROOM_CREATED:";
    private static final String JOIN_ROOM_PREFIX = "JOIN_ROOM:";
    private static final String JOIN_ROOM_SUCCESS_PREFIX = "JOIN_ROOM:SUCCESS:";
    private static final String GET_QUIZ_DATA_PREFIX = "GET_QUIZ_DATA:";
    private static final String QUIZ_DATA_PREFIX = "QUIZ_DATA:";
    private static final String QUIZ_DATA_SUCCESS_PREFIX = "QUIZ_DATA:SUCCESS:";
    private static final String QUIZ_DATA_FAILED_PREFIX = "QUIZ_DATA:FAILED:";
    private static final String GET_ROOM_INFO_PREFIX = "GET_ROOM_INFO:";
    private static final String ROOM_INFO_PREFIX = "ROOM_INFO:";

    private final QuizGUI quizGUI;
    private final PrintWriter out;
    private final BufferedReader in;
    private int quizId;
    private int roomId;

    public QuizListener(QuizGUI quizGUI, PrintWriter out, BufferedReader in) {
        this.quizGUI = quizGUI;
        this.out = out;
        this.in = in;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quizGUI.getCreateQuizButton()) {
            handleCreateQuiz();
        } else if (e.getSource() == quizGUI.getCreateRoomButton()) {
            handleCreateRoom();
        } else if (e.getSource() == quizGUI.getJoinRoomButton()) {
            handleJoinRoom();
        } else if (e.getSource() == quizGUI.getStartQuizButton()) {
            handleStartQuiz();
        }
    }

    private void handleCreateQuiz() {
        String quizName = JOptionPane.showInputDialog(
                quizGUI,
                "Enter quiz name (User: " + quizGUI.getUsername() + ")",
                "Create Quiz - User ID: " + quizGUI.getUserId(),
                JOptionPane.QUESTION_MESSAGE
        );

        if (quizName == null || quizName.isEmpty()) {
            JOptionPane.showMessageDialog(quizGUI, "Quiz name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String questions = JOptionPane.showInputDialog(
                quizGUI,
                "Enter questions in format: question|option1|option2|option3|option4|correctAnswer;...",
                "Enter Questions",
                JOptionPane.QUESTION_MESSAGE
        );

        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(quizGUI, "Questions cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println(CREATE_QUIZ_PREFIX + quizName + ":" + quizGUI.getUserId() + ":" + questions);
        try {
            String response = in.readLine();
            if (response == null) {
                showServerDisconnectedError();
                return;
            }

            if (response.startsWith(QUIZ_CREATED_PREFIX)) {
                quizId = Integer.parseInt(response.split(":")[1]);
                JOptionPane.showMessageDialog(quizGUI, "Quiz created successfully with ID: " + quizId);
            } else {
                JOptionPane.showMessageDialog(quizGUI, "Failed to create quiz: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Invalid create quiz response: " + response); // Log invalid response
            }
        } catch (IOException e) {
            showCommunicationError(e);
        }
    }

    private void handleCreateRoom() {
        String quizIdInput = JOptionPane.showInputDialog(quizGUI, "Enter quiz ID:");
        if (quizIdInput == null || quizIdInput.isEmpty()) {
            JOptionPane.showMessageDialog(quizGUI, "Quiz ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String roomName = JOptionPane.showInputDialog(quizGUI, "Enter room name:");
        if (roomName == null || roomName.isEmpty()) {
            JOptionPane.showMessageDialog(quizGUI, "Room name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println(CREATE_ROOM_PREFIX + roomName + ":" + quizIdInput + ":" + quizGUI.getUserId());
        try {
            String response = in.readLine();
            if (response == null) {
                showServerDisconnectedError();
                return;
            }

            if (response.startsWith(ROOM_CREATED_PREFIX)) {
                roomId = Integer.parseInt(response.split(":")[1]);
                JOptionPane.showMessageDialog(quizGUI,"Room created successfully with ID: " + roomId);
                // Refresh room list
                quizGUI.updateActiveRooms();
            } else {
                JOptionPane.showMessageDialog(quizGUI, "Failed to create room: " + response, "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Invalid create room response: " + response); // Log invalid response
            }
        } catch (IOException e) {
            showCommunicationError(e);
        }
    }

    private void handleJoinRoom() {
        String roomIdInput = JOptionPane.showInputDialog(quizGUI, "Enter room ID:");
        if (roomIdInput == null || roomIdInput.isEmpty()) {
            JOptionPane.showMessageDialog(quizGUI, "Room ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Integer.parseInt(roomIdInput);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(quizGUI, "Room ID must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            out.println(JOIN_ROOM_PREFIX + roomIdInput + ":" + quizGUI.getUserId());
            long startTime = System.currentTimeMillis();
            String response;
            while ((response = in.readLine()) != null) {
                if (System.currentTimeMillis() - startTime > JOIN_TIMEOUT) {
                    showError("Server response timeout");
                    return;
                }

                if (response.startsWith(JOIN_ROOM_SUCCESS_PREFIX)) {
                    this.roomId = Integer.parseInt(response.substring(JOIN_ROOM_SUCCESS_PREFIX.length()));
                    showSuccess("Joined room successfully! Room ID: " + roomId);
                    return;
                } else if (response.startsWith("ERROR")) {
                    showError("Failed to join: " + response.substring(6));
                    return;
                }
            }
        } catch (IOException e) {
            showCommunicationError(e);
        }
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(quizGUI, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void showSuccess(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(quizGUI, message, "Success", JOptionPane.INFORMATION_MESSAGE));
    }

    private void handleStartQuiz() {
        if (roomId == 0) {
            JOptionPane.showMessageDialog(quizGUI, "Please join a room first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println(GET_QUIZ_DATA_PREFIX + roomId);
        try {
            String response;
            long startTime = System.currentTimeMillis();
            while ((response = in.readLine()) != null) {
                if (System.currentTimeMillis() - startTime > JOIN_TIMEOUT) {
                    showError("Server response timeout");
                    return;
                }

                if (response.startsWith(QUIZ_DATA_PREFIX)) {
                    if (response.startsWith(QUIZ_DATA_SUCCESS_PREFIX)) {
                        String quizData = response.substring(QUIZ_DATA_PREFIX.length());
                        quizGUI.setVisible(false); // Hide the window instead of disposing it

                        // Get room info for display
                        out.println(GET_ROOM_INFO_PREFIX + roomId);
                        String roomInfo = in.readLine();

                        if (roomInfo != null && roomInfo.startsWith(ROOM_INFO_PREFIX)) {
                            String[] infoParts = roomInfo.substring(ROOM_INFO_PREFIX.length()).split(",");
                            SwingUtilities.invokeLater(() -> {
                                new Gameplay(quizData, roomId, quizGUI.getUsername(), quizGUI.getUserId(),
                                        infoParts[0], infoParts[1], out, in).setVisible(true);
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(quizGUI, "Failed to start quiz: " + response,
                                "Error", JOptionPane.ERROR_MESSAGE);
                        System.err.println("Invalid start quiz response: " + response); // Log invalid response
                    }
                    return;
                }
            }
        } catch (IOException e) {
            showCommunicationError(e);
        }
    }

    private void showCommunicationError(IOException e) {
        JOptionPane.showMessageDialog(quizGUI,
                "Failed to communicate with the server: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showServerDisconnectedError() {
        JOptionPane.showMessageDialog(quizGUI,
                "Server disconnected",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
