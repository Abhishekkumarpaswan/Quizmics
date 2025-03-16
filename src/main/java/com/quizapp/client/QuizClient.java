package com.quizapp.client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class QuizClient extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int userId;
    private int roomId;

    private JTextField usernameField, passwordField;
    private JButton loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton;
    private JTextArea quizArea;

    public QuizClient() {
        setTitle("Online Quiz Application");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20); // Use JPasswordField for password input
        loginButton = new JButton("Login");
        createQuizButton = new JButton("Create Quiz");
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        startQuizButton = new JButton("Start Quiz");
        quizArea = new JTextArea(10, 40);
        quizArea.setEditable(false); // Make quiz area read-only

        // Layout
        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(createQuizButton);
        panel.add(createRoomButton);
        panel.add(joinRoomButton);
        panel.add(startQuizButton);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(quizArea), BorderLayout.CENTER);

        // Event listeners
        loginButton.addActionListener(e -> login());
        createQuizButton.addActionListener(e -> createQuiz());
        createRoomButton.addActionListener(e -> createRoom());
        joinRoomButton.addActionListener(e -> joinRoom());
        startQuizButton.addActionListener(e -> startQuiz());

        // Connect to the server
        try {
            socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit if the server connection fails
        }
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("LOGIN:" + username + ":" + password);

        try {
            String response = in.readLine();
            if (response.startsWith("LOGIN_SUCCESS")) {
                userId = Integer.parseInt(response.split(":")[1]);
                JOptionPane.showMessageDialog(this, "Login successful!");
                enableQuizFeatures(); // Enable quiz-related features after login
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createQuiz() {
        String quizName = JOptionPane.showInputDialog(this, "Enter quiz name:");
        if (quizName == null || quizName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quiz name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("CREATE_QUIZ:" + quizName + ":" + userId);

        try {
            String response = in.readLine();
            if (response.startsWith("QUIZ_CREATED")) {
                JOptionPane.showMessageDialog(this, "Quiz created successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create quiz: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createRoom() {
        String roomName = JOptionPane.showInputDialog(this, "Enter room name:");
        if (roomName == null || roomName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String quizId = JOptionPane.showInputDialog(this, "Enter quiz ID:");
        if (quizId == null || quizId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Quiz ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("CREATE_ROOM:" + roomName + ":" + quizId);

        try {
            String response = in.readLine();
            if (response.startsWith("ROOM_CREATED")) {
                JOptionPane.showMessageDialog(this, "Room created successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create room: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void joinRoom() {
        String roomId = JOptionPane.showInputDialog(this, "Enter room ID:");
        if (roomId == null || roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("JOIN_ROOM:" + roomId + ":" + userId);

        try {
            String response = in.readLine();
            if (response.startsWith("JOINED_ROOM")) {
                this.roomId = Integer.parseInt(roomId);
                JOptionPane.showMessageDialog(this, "Joined room successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to join room: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startQuiz() {
        if (roomId == 0) {
            JOptionPane.showMessageDialog(this, "You must join a room before starting a quiz!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println("START_QUIZ:" + roomId);

        try {
            String response = in.readLine();
            if (response.startsWith("QUESTIONS")) {
                quizArea.setText(response.split(":")[1]);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to start quiz: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Enables quiz-related features after successful login.
     */
    private void enableQuizFeatures() {
        createQuizButton.setEnabled(true);
        createRoomButton.setEnabled(true);
        joinRoomButton.setEnabled(true);
        startQuizButton.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizClient client = new QuizClient();
            client.setVisible(true);
        });
    }
}