package com.quizapp.model;

public class Quiz {
    private int quizId;
    private String quizName;
    private int createdBy;

    // Constructor
    public Quiz(int quizId, String quizName, int createdBy) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.createdBy = createdBy;
    }

    // Getters
    public int getQuizId() {
        return quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    // Setters
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}