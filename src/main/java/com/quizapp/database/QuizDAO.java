// Handles quiz-related database operations

package com.quizapp.database;

import com.quizapp.model.Quiz;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {
    public List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String query = "SELECT * FROM quizzes";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quizzes.add(new Quiz(rs.getInt("quiz_id"), rs.getString("quiz_name"), rs.getInt("created_by")));
            }
        }
        return quizzes;
    }

    public int createQuiz(String quizName, int createdBy) throws SQLException {
        String query = "INSERT INTO quizzes (quiz_name, created_by) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quizName);
            stmt.setInt(2, createdBy);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
}