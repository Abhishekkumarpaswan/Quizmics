package com.quizapp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

public class QuizListener implements ActionListener {
    private QuizGUI quizGUI;
    private PrintWriter out;
    private int userId;
    private int roomId;

    public QuizListener(QuizGUI quizGUI, PrintWriter out) {
        this.quizGUI = quizGUI;
        this.out = out;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quizGUI.getLoginButton()) {
            String username = quizGUI.getUsernameField().getText();
            String password = new String(quizGUI.getPasswordField().getPassword()); // Use getPassword() for JPasswordField
            out.println("LOGIN:" + username + ":" + password);
        } else if (e.getSource() == quizGUI.getCreateQuizButton()) {
            String quizName = JOptionPane.showInputDialog("Enter quiz name:");
            if (quizName == null || quizName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Quiz name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            out.println("CREATE_QUIZ:" + quizName + ":" + userId);
        } else if (e.getSource() == quizGUI.getCreateRoomButton()) {
            String roomName = JOptionPane.showInputDialog("Enter room name:");
            if (roomName == null || roomName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Room name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String quizId = JOptionPane.showInputDialog("Enter quiz ID:");
            if (quizId == null || quizId.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Quiz ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            out.println("CREATE_ROOM:" + roomName + ":" + quizId);
        } else if (e.getSource() == quizGUI.getJoinRoomButton()) {
            String roomId = JOptionPane.showInputDialog("Enter room ID:");
            if (roomId == null || roomId.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Room ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.roomId = Integer.parseInt(roomId); // Update the room ID
            out.println("JOIN_ROOM:" + roomId + ":" + userId);
        } else if (e.getSource() == quizGUI.getStartQuizButton()) {
            if (roomId == 0) {
                JOptionPane.showMessageDialog(null, "You must join a room before starting a quiz!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            out.println("START_QUIZ:" + roomId);
        }
    }
}