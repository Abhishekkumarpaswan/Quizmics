package com.quizapp.model;

public class Result {
    private int resultId;
    private int userId;
    private int quizId;
    private int score;

    // Constructor
    public Result(int resultId, int userId, int quizId, int score) {
        this.resultId = resultId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
    }

    // Getters
    public int getResultId() {
        return resultId;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuizId() {
        return quizId;
    }

    public int getScore() {
        return score;
    }

    // Setters
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setScore(int score) {
        this.score = score;
    }
}