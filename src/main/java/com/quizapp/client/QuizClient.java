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

    private JTextField usernameField;
    private JPasswordField passwordField; // Use JPasswordField for password input
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
        panel.add(passwordField); // Ensure this is JPasswordField
        panel.add(loginButton);
        panel.add(createQuizButton);
        panel.add(createRoomButton);
        panel.add(joinRoomButton);
        panel.add(startQuizButton);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(quizArea), BorderLayout.CENTER);

        // Connect to the server
        try {
            socket = new Socket("localhost", 12345); // Changed to 12345 to match server
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Initialize QuizGUI and QuizListener
            QuizGUI quizGUI = new QuizGUI(usernameField, passwordField, loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton, quizArea);
            QuizListener quizListener = new QuizListener(quizGUI, out, in);

            // Action listeners for the buttons
            loginButton.addActionListener(quizListener);
            createQuizButton.addActionListener(quizListener);
            createRoomButton.addActionListener(quizListener);
            joinRoomButton.addActionListener(quizListener);
            startQuizButton.addActionListener(quizListener);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit if the server connection fails
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizClient client = new QuizClient();
            client.setVisible(true);
        });
    }
}
