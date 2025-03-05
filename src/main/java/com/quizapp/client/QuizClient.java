// Main client class with GUI

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

        // Event listeners
        loginButton.addActionListener(e -> login());
        createQuizButton.addActionListener(e -> createQuiz());
        createRoomButton.addActionListener(e -> createRoom());
        joinRoomButton.addActionListener(e -> joinRoom());
        startQuizButton.addActionListener(e -> startQuiz());

        // Connect to the server
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        out.println("LOGIN:" + username + ":" + password);

        try {
            String response = in.readLine();
            if (response.startsWith("LOGIN_SUCCESS")) {
                userId = Integer.parseInt(response.split(":")[1]);
                JOptionPane.showMessageDialog(this, "Login successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Login failed!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createQuiz() {
        String quizName = JOptionPane.showInputDialog(this, "Enter quiz name:");
        out.println("CREATE_QUIZ:" + quizName + ":" + userId);

        try {
            String response = in.readLine();
            if (response.startsWith("QUIZ_CREATED")) {
                JOptionPane.showMessageDialog(this, "Quiz created successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createRoom() {
        String roomName = JOptionPane.showInputDialog(this, "Enter room name:");
        String quizId = JOptionPane.showInputDialog(this, "Enter quiz ID:");
        out.println("CREATE_ROOM:" + roomName + ":" + quizId);

        try {
            String response = in.readLine();
            if (response.startsWith("ROOM_CREATED")) {
                JOptionPane.showMessageDialog(this, "Room created successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void joinRoom() {
        String roomId = JOptionPane.showInputDialog(this, "Enter room ID:");
        out.println("JOIN_ROOM:" + roomId + ":" + userId);

        try {
            String response = in.readLine();
            if (response.startsWith("JOINED_ROOM")) {
                this.roomId = Integer.parseInt(roomId);
                JOptionPane.showMessageDialog(this, "Joined room successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startQuiz() {
        out.println("START_QUIZ:" + roomId);

        try {
            String response = in.readLine();
            if (response.startsWith("QUESTIONS")) {
                quizArea.setText(response.split(":")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new QuizClient().setVisible(true);
    }
}