package com.quizapp.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Authentication extends JFrame {
    private static final String LOGIN_PREFIX = "LOGIN:";
    private static final String LOGIN_SUCCESS_PREFIX = "LOGIN_SUCCESS:";
    private static final String REGISTER_PREFIX = "REGISTER:";
    private static final String REGISTER_SUCCESS_PREFIX = "REGISTER_SUCCESS:";
    private static final String LOGIN_FAILED_PREFIX = "LOGIN_FAILED";
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Socket socket;
    private PrintWriter out;
    private JFrame registerFrame;
    private BufferedReader in;
    private int userId;
    private boolean authenticated;
    private AuthCallback callback;
    private String username;
    public interface AuthCallback {
        void onAuthenticationComplete();
    }

    public Authentication(Socket socket, PrintWriter out, BufferedReader in, AuthCallback callback) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        this.callback = callback;

        setTitle("QuizApp - Authentication");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        openLoginFrame();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public int getUserId() {
        return userId;
    }

    private void handleLogin() {
        this.username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println(LOGIN_PREFIX + username + ":" + password);

        try {
            String response = in.readLine();
            System.out.println("Server response: " + response); // Debug statement
            if (response.startsWith(LOGIN_SUCCESS_PREFIX)) {
                userId = Integer.parseInt(response.substring(LOGIN_SUCCESS_PREFIX.length()));
                authenticated = true;
                JOptionPane.showMessageDialog(this, "Login successful!");
                setVisible(false); // Hide the window instead of disposing it
                if (callback != null) {
                    callback.onAuthenticationComplete();
                }
            } else if (response.startsWith(LOGIN_FAILED_PREFIX)) {
                JOptionPane.showMessageDialog(this, "Login failed! Invalid username or password.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Invalid login response: " + response); // Log invalid response
            } else {
                JOptionPane.showMessageDialog(this, "Login failed! Unexpected server response.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Unexpected login response: " + response); // Log unexpected response
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with the server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getUsername() {
        return username;
    }

    private void openLoginFrame() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(24, 178, 186);
                Color color2 = new Color(232, 77, 211);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel(" Welcome to QuizApp", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        usernameField = createStyledTextField("Username");
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(usernameField, gbc);

        passwordField = createStyledPasswordField("Password");
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        loginButton = createStyledButton("Login", new Color(34, 153, 84));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 3;
        panel.add(loginButton, gbc);

        registerButton = createStyledButton("Register", new Color(52, 152, 219));
        registerButton.addActionListener(this::actionPerformed);
        gbc.gridy = 4;
        panel.add(registerButton, gbc);

        add(panel);
        revalidate();
        repaint();
    }

    private void openRegisterFrame() {
        registerFrame = new JFrame("Register - QuizApp");
        registerFrame.setSize(400, 300);
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setLocationRelativeTo(this);

        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcRegister = new GridBagConstraints();
        gbcRegister.insets = new Insets(10, 10, 10, 10);

        JTextField registerUsernameField = createStyledTextField("Username");
        gbcRegister.gridy = 0;
        registerPanel.add(registerUsernameField, gbcRegister);

        JPasswordField registerPasswordField = createStyledPasswordField("Password");
        gbcRegister.gridy = 1;
        registerPanel.add(registerPasswordField, gbcRegister);

        JPasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");
        gbcRegister.gridy = 2;
        registerPanel.add(confirmPasswordField, gbcRegister);

        JButton submitRegisterButton = createStyledButton("Submit", new Color(34, 153, 84));
        gbcRegister.gridy = 3;
        registerPanel.add(submitRegisterButton, gbcRegister);

        submitRegisterButton.addActionListener(e -> handleRegister(
            registerUsernameField.getText(),
            new String(registerPasswordField.getPassword()),
            new String(confirmPasswordField.getPassword())
        ));

        registerFrame.add(registerPanel);
        registerFrame.setVisible(true);
    }

    private void handleRegister(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println(REGISTER_PREFIX + username + ":" + password);

        try {
            String response = in.readLine();
            if (response.startsWith(REGISTER_SUCCESS_PREFIX)) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                registerFrame.dispose();
                SwingUtilities.invokeLater(() -> {
                    setVisible(true); // Show the login GUI
                    usernameField.setText(""); // Clear the username field
                    passwordField.setText(""); // Clear the password field
                });
//                if (callback != null) {
//                    callback.onAuthenticationComplete();
//                }
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed: " +
                                (response.startsWith("REGISTER_ERROR:") ? response.substring(15) : response),
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.err.println("Invalid register response: " + response); // Log invalid response
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to communicate with server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setBackground(new Color(220, 220, 220));
        field.setOpaque(true);

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.GRAY);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        field.setBackground(new Color(220, 220, 220));
        field.setOpaque(true);
        field.setEchoChar('●');
        field.setText(placeholder);

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });

        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
        });

        return button;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            openRegisterFrame();
        }
    }
}
