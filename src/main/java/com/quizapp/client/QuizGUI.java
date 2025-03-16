package com.quizapp.client;

import javax.swing.*;
import java.awt.*;

public class QuizGUI extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField; // Use JPasswordField for password input
    private JButton loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton;
    private JTextArea quizArea;

    public QuizGUI() {
        setLayout(new BorderLayout());

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
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5)); // Grid layout for input fields
        inputPanel.add(new JLabel("Username:"));
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Flow layout for buttons
        buttonPanel.add(loginButton);
        buttonPanel.add(createQuizButton);
        buttonPanel.add(createRoomButton);
        buttonPanel.add(joinRoomButton);
        buttonPanel.add(startQuizButton);

        JPanel topPanel = new JPanel(new BorderLayout()); // Combine input and button panels
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(quizArea), BorderLayout.CENTER); // Add scrollable quiz area
    }

    // Getters for components
    public JTextField getUsernameField() {
        return usernameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getCreateQuizButton() {
        return createQuizButton;
    }

    public JButton getCreateRoomButton() {
        return createRoomButton;
    }

    public JButton getJoinRoomButton() {
        return joinRoomButton;
    }

    public JButton getStartQuizButton() {
        return startQuizButton;
    }

    public JTextArea getQuizArea() {
        return quizArea;
    }
}