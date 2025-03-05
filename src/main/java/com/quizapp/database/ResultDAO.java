// Handles result-related database operations

package com.quizapp.database;

import com.quizapp.model.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResultDAO {
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
}