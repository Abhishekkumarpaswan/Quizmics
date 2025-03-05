// Handles client-side events
package com.quizapp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;

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
            String password = quizGUI.getPasswordField().getText();
            out.println("LOGIN:" + username + ":" + password);
        } else if (e.getSource() == quizGUI.getCreateQuizButton()) {
            String quizName = JOptionPane.showInputDialog("Enter quiz name:");
            out.println("CREATE_QUIZ:" + quizName + ":" + userId);
        } else if (e.getSource() == quizGUI.getCreateRoomButton()) {
            String roomName = JOptionPane.showInputDialog("Enter room name:");
            String quizId = JOptionPane.showInputDialog("Enter quiz ID:");
            out.println("CREATE_ROOM:" + roomName + ":" + quizId);
        } else if (e.getSource() == quizGUI.getJoinRoomButton()) {
            String roomId = JOptionPane.showInputDialog("Enter room ID:");
            out.println("JOIN_ROOM:" + roomId + ":" + userId);
        } else if (e.getSource() == quizGUI.getStartQuizButton()) {
            out.println("START_QUIZ:" + roomId);
        }
    }
}