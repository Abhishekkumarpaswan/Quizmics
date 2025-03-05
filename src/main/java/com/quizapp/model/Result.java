// Result Model Class

package com.quizapp.model;

public class Result {
    private int resultId;
    private int userId;
    private int quizId;
    private int score;

    public Result(int resultId, int userId, int quizId, int score) {
        this.resultId = resultId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }

    // Getters and setters
}