// Quiz Model Class

package com.quizapp.model;

public class Quiz {
    private int quizId;
    private String quizName;
    private int createdBy;

    public Quiz(int quizId, String quizName, int createdBy) {
        this.quizId = quizId;
        this.quizName = quizName;
        this.createdBy = createdBy;
    }

    // Getters and setters
}