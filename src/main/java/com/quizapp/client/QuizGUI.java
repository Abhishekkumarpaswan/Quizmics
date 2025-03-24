package com.quizapp.client;

import javax.swing.*;

public class QuizGUI {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton;
    private JTextArea quizArea;

    public QuizGUI(JTextField usernameField, JPasswordField passwordField, JButton loginButton, JButton createQuizButton, JButton createRoomButton, JButton joinRoomButton, JButton startQuizButton, JTextArea quizArea) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.loginButton = loginButton;
        this.createQuizButton = createQuizButton;
        this.createRoomButton = createRoomButton;
        this.joinRoomButton = joinRoomButton;
        this.startQuizButton = startQuizButton;
        this.quizArea = quizArea;
    }

    // Getters for the components
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

    // Method to enable quiz features after successful login
    public void enableQuizFeatures() {
        createQuizButton.setEnabled(true);
        createRoomButton.setEnabled(true);
        joinRoomButton.setEnabled(true);
        startQuizButton.setEnabled(true);
    }
}