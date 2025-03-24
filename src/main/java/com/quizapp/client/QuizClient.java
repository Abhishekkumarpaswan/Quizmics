package com.quizapp.client;
//
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
//
//public class QuizClient extends JFrame {
//    private Socket socket;
//    private PrintWriter out;
//    private BufferedReader in;
//    private int userId;
//    private int roomId;
//
//    private JTextField usernameField;
//    private JPasswordField passwordField; // Use JPasswordField for password input
//    private JButton loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton;
//    private JTextArea quizArea;
//
//    public QuizClient() {
//        setTitle("Online Quiz Application");
//        setSize(600, 500);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        // Initialize components
//        usernameField = new JTextField(20);
//        passwordField = new JPasswordField(20); // Use JPasswordField for password input
//        loginButton = new JButton("Login");
//        createQuizButton = new JButton("Create Quiz");
//        createRoomButton = new JButton("Create Room");
//        joinRoomButton = new JButton("Join Room");
//        startQuizButton = new JButton("Start Quiz");
//        quizArea = new JTextArea(10, 40);
//        quizArea.setEditable(false); // Make quiz area read-only
//
//        // Layout
//        JPanel panel = new JPanel();
//        panel.add(new JLabel("Username:"));
//        panel.add(usernameField);
//        panel.add(new JLabel("Password:"));
//        panel.add(passwordField); // Ensure this is JPasswordField
//        panel.add(loginButton);
//        panel.add(createQuizButton);
//        panel.add(createRoomButton);
//        panel.add(joinRoomButton);
//        panel.add(startQuizButton);
//        add(panel, BorderLayout.NORTH);
//        add(new JScrollPane(quizArea), BorderLayout.CENTER);
//
//        // Connect to the server
//        try {
//            socket = new Socket("localhost", 12345); // Changed to 12345 to match server
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            // Initialize QuizGUI and QuizListener
//            QuizGUI quizGUI = new QuizGUI(usernameField, passwordField, loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton, quizArea);
//            QuizListener quizListener = new QuizListener(quizGUI, out, in);
//
//            // Action listeners for the buttons
//            loginButton.addActionListener(quizListener);
//            createQuizButton.addActionListener(quizListener);
//            createRoomButton.addActionListener(quizListener);
//            joinRoomButton.addActionListener(quizListener);
//            startQuizButton.addActionListener(quizListener);
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(this, "Failed to connect to the server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            System.exit(1); // Exit if the server connection fails
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            QuizClient client = new QuizClient();
//            client.setVisible(true);
//        });
//    }
//}

public class QuizClient extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int userId;
    private int roomId;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createQuizButton, createRoomButton, joinRoomButton, startQuizButton;
    private JTextArea quizArea;

    public QuizClient() {
        setTitle("Online Quiz Application");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        // Panel for login section
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createTitledBorder("User Login"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginButton = new JButton("Login");
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginPanel.add(loginButton, gbc);

        // Panel for quiz controls
        JPanel controlPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Quiz Controls"));

        createQuizButton = new JButton("Create Quiz");
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        startQuizButton = new JButton("Start Quiz");

        // Style buttons
        JButton[] buttons = {createQuizButton, createRoomButton, joinRoomButton, startQuizButton};
        for (JButton button : buttons) {
            button.setBackground(new Color(60, 179, 113)); // Green color
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            controlPanel.add(button);
        }

        // Quiz Area
        quizArea = new JTextArea(10, 40);
        quizArea.setEditable(false);
        quizArea.setFont(new Font("Arial", Font.PLAIN, 14));
        quizArea.setBorder(BorderFactory.createTitledBorder("Quiz Questions"));

        // Adding components to the frame
        add(loginPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(new JScrollPane(quizArea), BorderLayout.SOUTH);

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
            QuizClient q = new QuizClient();
            q.setVisible(true);
        });
    }
}