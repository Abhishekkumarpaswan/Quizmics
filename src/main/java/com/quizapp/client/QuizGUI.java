// Handles GUI components

package com.quizapp.client;

import javax.swing.*;
import java.awt.*;

public class QuizGUI extends JPanel {
    private JTextField usernameField, passwordField;
    private JButton loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton;
    private JTextArea quizArea;

    public QuizGUI() {
        setLayout(new BorderLayout());

        // Initialize components
        usernameField = new JTextField(20);
        passwordField = new JTextField(20);
        loginButton = new JButton("Login");
        createQuizButton = new JButton("Create Quiz");
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        startQuizButton = new JButton("Start Quiz");
        quizArea = new JTextArea(10, 40);

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
    }

    public JTextField getUsernameField() {
        return usernameField;
    }

    public JTextField getPasswordField() {
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