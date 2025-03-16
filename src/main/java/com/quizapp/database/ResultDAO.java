package com.quizapp.database;

import com.quizapp.model.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {
    /**
     * Saves a quiz result in the database.
     *
     * @param userId The ID of the user who took the quiz.
     * @param quizId The ID of the quiz.
     * @param score  The score achieved by the user.
     * @throws SQLException If a database access error occurs.
     */
    public void saveResult(int userId, int quizId, int score) throws SQLException {
        String query = "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            stmt.setInt(3, score);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all results for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Result objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Result> getResultsByUser(int userId) throws SQLException {
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM results WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Result(
                            rs.getInt("result_id"),
                            rs.getInt("user_id"),
                            rs.getInt("quiz_id"),
                            rs.getInt("score")
                    ));
                }
            }
        }
        return results;
    }

    /**
     * Retrieves all results for a specific quiz.
     *
     * @param quizId The ID of the quiz.
     * @return A list of Result objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Result> getResultsByQuiz(int quizId) throws SQLException {
        List<Result> results = new ArrayList<>();
        String query = "SELECT * FROM results WHERE quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new Result(
                            rs.getInt("result_id"),
                            rs.getInt("user_id"),
                            rs.getInt("quiz_id"),
                            rs.getInt("score")
                    ));
                }
            }
        }
        return results;
    }

    /**
     * Deletes a result by its ID.
     *
     * @param resultId The ID of the result to delete.
     * @return True if the result was deleted, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean deleteResult(int resultId) throws SQLException {
        String query = "DELETE FROM results WHERE result_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, resultId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Return true if the result was deleted
        }
    }
}