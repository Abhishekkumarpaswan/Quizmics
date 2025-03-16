package com.quizapp.database;

import com.quizapp.model.Quiz;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {
    /**
     * Retrieves all quizzes from the database.
     *
     * @return A list of Quiz objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String query = "SELECT * FROM quizzes";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                quizzes.add(new Quiz(
                        rs.getInt("quiz_id"),
                        rs.getString("quiz_name"),
                        rs.getInt("created_by")
                ));
            }
        }
        return quizzes;
    }

    /**
     * Creates a new quiz in the database.
     *
     * @param quizName  The name of the quiz.
     * @param createdBy The ID of the user who created the quiz.
     * @return The ID of the newly created quiz, or -1 if the operation fails.
     * @throws SQLException If a database access error occurs.
     */
    public int createQuiz(String quizName, int createdBy) throws SQLException {
        String query = "INSERT INTO quizzes (quiz_name, created_by) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quizName);
            stmt.setInt(2, createdBy);
            stmt.executeUpdate();

            // Retrieve the generated quiz ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1; // Return -1 if the operation fails
    }

    /**
     * Retrieves a quiz by its ID.
     *
     * @param quizId The ID of the quiz.
     * @return The Quiz object, or null if the quiz is not found.
     * @throws SQLException If a database access error occurs.
     */
    public Quiz getQuizById(int quizId) throws SQLException {
        String query = "SELECT * FROM quizzes WHERE quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Quiz(
                            rs.getInt("quiz_id"),
                            rs.getString("quiz_name"),
                            rs.getInt("created_by")
                    );
                }
            }
        }
        return null; // Return null if the quiz is not found
    }

    /**
     * Deletes a quiz by its ID.
     *
     * @param quizId The ID of the quiz to delete.
     * @return True if the quiz was deleted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteQuiz(int quizId) throws SQLException {
        String query = "DELETE FROM quizzes WHERE quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if the quiz was deleted
        }
    }
}