package com.quizapp.client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class QuizClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Authentication authentication;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuizClient client = new QuizClient();
            client.run();
        });
    }

    public void run() {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            authentication = new Authentication(socket, out, in, () -> {
                if (authentication.isAuthenticated()) {
                    int userId = authentication.getUserId();
                    String username = authentication.getUsername();
                    SwingUtilities.invokeLater(() -> {
                        QuizGUI quizGUI = new QuizGUI(userId, username);
                        quizGUI.setStreams(out, in);
                        QuizListener quizListener = new QuizListener(quizGUI, out, in);
                        quizGUI.addListeners(quizListener);
                        quizGUI.enableQuizFeatures();
                        quizGUI.setVisible(true);
                    });
                } else {
                    handleLogout();
                }
            });

            SwingUtilities.invokeLater(() -> authentication.setVisible(true));

        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "Failed to connect to the server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE));
            closeResources();
            System.exit(1);
        }
    }

    private void handleLogout() {
        try {
            out.println("LOGOUT:" + authentication.getUsername());
            String response = in.readLine();
            if ("LOGOUT_SUCCESS".equals(response)) {
                authentication.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Logout failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to communicate with the server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error closing resources: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
