package com.quizapp.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.*;
public class Gameplay extends JFrame implements ActionListener {
    private JTextArea questionArea;
    private JRadioButton[] optionButtons;
    private ButtonGroup buttonGroup;
    private JButton nextButton;
    private JButton backButton;
    private JButton submitButton;
    private JButton backToQuizGUIButton;
    private String[] questions;
    private String[][] options;
    private String[] correctAnswers;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int roomId;
    private int userId;
    private String username;
    private PrintWriter out;
    private BufferedReader in;
    private Timer playerUpdateTimer;
    private JList<String> playersList;
    private DefaultListModel<String> playersModel;
    private String[] userSelections;
    private static final String PLAYER_LIST_PREFIX = "PLAYER_LIST:";
    private static final String GET_PLAYER_LIST_PREFIX = "GET_PLAYER_LIST:";

    public Gameplay(String quizData, int roomId, String username, int userId,
                    String roomName, String quizName, PrintWriter out, BufferedReader in) {
        this.in = in;
        this.out = out;
        this.roomId = roomId;
        this.userId = userId;
        this.username = username;
        this.questions = quizData.split(";");
        this.userSelections = new String[questions.length];

        setTitle("Quiz Gameplay - Room: " + roomName);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel with room and quiz info
        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        infoPanel.add(new JLabel("Room: " + roomName + " (ID: " + roomId + ")"));
        infoPanel.add(new JLabel("Quiz: " + quizName));
        add(infoPanel, BorderLayout.NORTH);

        // Main question panel
        JPanel questionPanel = createQuestionPanel();
        add(questionPanel, BorderLayout.CENTER);

        // Right panel with players
        JPanel playersPanel = new JPanel(new BorderLayout());
        playersPanel.setBorder(BorderFactory.createTitledBorder("Players in Room"));
        playersModel = new DefaultListModel<>();
        playersModel.addElement(username + " (You)");
        playersList = new JList<>(playersModel);
        playersPanel.add(new JScrollPane(playersList), BorderLayout.CENTER);
        add(playersPanel, BorderLayout.EAST);

        // Bottom navigation panel
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.SOUTH);

        backToQuizGUIButton = new JButton("Back to QuizGUI");
        backToQuizGUIButton.addActionListener(e -> handleBackToQuizGUI());
        navPanel.add(backToQuizGUIButton);

        parseQuizData(quizData);
        loadQuestion(currentQuestionIndex);
        startPlayerUpdates();
    }

    private void handleBackToQuizGUI() {
        stopPlayerUpdates();
        out.println("LEAVE_ROOM:" + roomId + ":" + userId); // Notify server to remove client from room
        setVisible(false); // Hide the window instead of disposing it
        SwingUtilities.invokeLater(() -> {
            QuizGUI quizGUI = new QuizGUI(userId, username);
            quizGUI.setStreams(out, in);
            quizGUI.enableQuizFeatures(); // Re-enable buttons
            quizGUI.setVisible(true);
        });
    }

    private void startPlayerUpdates() {
        playerUpdateTimer = new Timer(3000, e -> updatePlayerList());
        playerUpdateTimer.start();
    }

    private void stopPlayerUpdates() {
        if (playerUpdateTimer != null) {
            playerUpdateTimer.stop();
        }
    }

    private void updatePlayerList() {
        if (out == null || in == null) {
            SwingUtilities.invokeLater(() -> playersModel.clear());
            return;
        }

        try {
            out.println(GET_PLAYER_LIST_PREFIX + roomId);
            String response = in.readLine();

            SwingUtilities.invokeLater(() -> {
                synchronized (playersModel) {
                    playersModel.clear(); // Clear previous content

                    if (response == null || response.startsWith("ERROR")) {
                        playersModel.addElement("Error fetching player list");
                        return;
                    }

                    if (response.startsWith(PLAYER_LIST_PREFIX)) {
                        String[] players = response.substring(PLAYER_LIST_PREFIX.length()).split(",");
                        for (String player : players) {
                            playersModel.addElement(player);
                        }
                    }
                }
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> playersModel.addElement("Error fetching player list: " + e.getMessage()));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            if (currentQuestionIndex > 0) {
                saveUserSelection();
                currentQuestionIndex--;
                loadQuestion(currentQuestionIndex);
            }
        } else if (e.getSource() == nextButton) {
            if (currentQuestionIndex < questions.length - 1) {
                saveUserSelection();
                currentQuestionIndex++;
                loadQuestion(currentQuestionIndex);
            }
        } else if (e.getSource() == submitButton) {
            saveUserSelection();
            calculateScore();
            showFinalResult();
        } else if (e.getSource() == backToQuizGUIButton) {
            handleBackToQuizGUI();
        }
    }

    private void saveUserSelection() {
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                userSelections[currentQuestionIndex] = optionButtons[i].getText();
                break;
            }
        }
    }

    private void calculateScore() {
        score = 0;
        for (int i = 0; i < questions.length; i++) {
            if (userSelections[i] != null && userSelections[i].equals(correctAnswers[i])) {
                score++;
            }
        }
    }

    private void showFinalResult() {
        JOptionPane.showMessageDialog(this, "Your score: " + score + "/" + questions.length);
        // Do not hide the window
        // Optionally, you can navigate back to the main QuizGUI or close the application
    }

    private JPanel createQuestionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        panel.add(new JScrollPane(questionArea), BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        optionButtons = new JRadioButton[4];
        buttonGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            buttonGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }
        panel.add(optionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        backButton = new JButton("Back");
        backButton.addActionListener(this);
        panel.add(backButton);

        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        panel.add(nextButton);

        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        panel.add(submitButton);

        return panel;
    }

    private void parseQuizData(String quizData) {
        String[] questionEntries = quizData.split(";");
        questions = new String[questionEntries.length];
        options = new String[questionEntries.length][4];
        correctAnswers = new String[questionEntries.length];

        for (int i = 0; i < questionEntries.length; i++) {
            String[] parts = questionEntries[i].split("\\|");
            questions[i] = parts[0];
            for (int j = 0; j < 4; j++) {
                options[i][j] = parts[j + 1];
            }
            correctAnswers[i] = parts[5];
        }
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.length) {
            return;
        }
        questionArea.setText(questions[index]);
        buttonGroup.clearSelection(); // Ensure no radio button is pre-selected
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options[index][i]);
            optionButtons[i].setSelected(false);
        }
    }
}
