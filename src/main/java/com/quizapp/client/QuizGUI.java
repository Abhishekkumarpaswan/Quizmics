package com.quizapp.client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.event.ActionListener;

public class QuizGUI extends JFrame {
    private PrintWriter out;
    private BufferedReader in;
    private JButton createQuizButton, createRoomButton, joinRoomButton, startQuizButton, logoutButton;
    private JTextArea quizArea;
    private final int userId;
    private final String username;
    private Timer roomUpdateTimer;
    private static final String ACTIVE_ROOMS_PREFIX = "ACTIVE_ROOMS:";
    private static final String GET_ACTIVE_ROOMS_PREFIX = "GET_ACTIVE_ROOMS";

    public QuizGUI(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Online Quiz Application - User: " + username);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (out != null) {
                    out.println("QUIT");
                }
            }
        });

        initializeComponents();
    }

    public void initializeComponents() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create user info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.add(new JLabel("User: " + username + " | ID: " + userId));
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        userPanel.setBackground(new Color(240, 240, 240));

        // Create control panel
        JPanel controlPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Quiz Controls"));

        // Create and style buttons
        createQuizButton = new JButton("Create Quiz");
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        startQuizButton = new JButton("Start Quiz");
        logoutButton = new JButton("Logout");

        JButton[] buttons = {createQuizButton, createRoomButton, joinRoomButton, startQuizButton, logoutButton};
        for (JButton button : buttons) {
            button.setBackground(new Color(60, 179, 113));
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setEnabled(false);
            controlPanel.add(button);
        }

        logoutButton.setBackground(new Color(60, 179, 113));
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.addActionListener(e -> handleLogout());
        controlPanel.add(logoutButton);

        // Create quiz area
        quizArea = new JTextArea(10, 40);
        quizArea.setEditable(false);
        quizArea.setFont(new Font("Arial", Font.PLAIN, 14));
        quizArea.setBorder(BorderFactory.createTitledBorder("Active Rooms"));
        JScrollPane scrollPane = new JScrollPane(quizArea);
        startRoomUpdates();

        // Add components to main panel
        mainPanel.add(userPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    private void startRoomUpdates() {
        roomUpdateTimer = new Timer(3000, e -> updateActiveRooms());
        roomUpdateTimer.start();
    }

    public void stopRoomUpdates() {
        if (roomUpdateTimer != null) {
            roomUpdateTimer.stop();
        }
    }

    public void updateActiveRooms() {
        if (out == null || in == null) {
            SwingUtilities.invokeLater(() ->
                    quizArea.setText("Not connected to server"));
            return;
        }

        try {
            out.println(GET_ACTIVE_ROOMS_PREFIX);
            String response = in.readLine();

            SwingUtilities.invokeLater(() -> {
                quizArea.setText(""); // Clear previous content

                if (response == null || response.startsWith("ERROR") ||
                        response.equals(ACTIVE_ROOMS_PREFIX + "EMPTY")) {
                    quizArea.setText("No active rooms available");
                    return;
                }

                if (response.startsWith(ACTIVE_ROOMS_PREFIX)) {
                    String[] rooms = response.substring(ACTIVE_ROOMS_PREFIX.length()).split(";");
                    for (String room : rooms) {
                        quizArea.append(room + "\n");
                    }
                }
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(() ->
                    quizArea.setText("Error fetching active rooms: " + e.getMessage()));
        }
    }

    public void setStreams(PrintWriter out, BufferedReader in) {
        this.out = out;
        this.in = in;
    }

    public void addListeners(ActionListener listener) {
        createQuizButton.addActionListener(listener);
        createRoomButton.addActionListener(listener);
        joinRoomButton.addActionListener(listener);
        startQuizButton.addActionListener(listener);
        logoutButton.addActionListener(listener);
    }

    public void enableQuizFeatures() {
        createQuizButton.setEnabled(true);
        createRoomButton.setEnabled(true);
        joinRoomButton.setEnabled(true);
        startQuizButton.setEnabled(true);
        logoutButton.setEnabled(true);
    }

    public void disableQuizFeatures() {
        createQuizButton.setEnabled(false);
        createRoomButton.setEnabled(false);
        joinRoomButton.setEnabled(false);
        startQuizButton.setEnabled(false);
        logoutButton.setEnabled(false);
    }

    private void handleLogout() {
        try {
            out.println("LOGOUT:" + username);
            String response = in.readLine();
            if ("LOGOUT_SUCCESS".equals(response)) {
                JOptionPane.showMessageDialog(this, "Logout successful!");
                setVisible(false); // Hide the window instead of disposing it
                System.exit(0); // Exit the application
            } else {
                JOptionPane.showMessageDialog(this, "Logout failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with the server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBackToQuizGUI() {
        stopRoomUpdates();
        setVisible(false); // Hide the window instead of disposing it
        SwingUtilities.invokeLater(() -> {
            QuizGUI quizGUI = new QuizGUI(userId, username);
            quizGUI.setStreams(out, in);
            quizGUI.enableQuizFeatures(); // Re-enable buttons
            quizGUI.setVisible(true);
        });
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

    public JButton getLogoutButton() {
        return logoutButton;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }
}
