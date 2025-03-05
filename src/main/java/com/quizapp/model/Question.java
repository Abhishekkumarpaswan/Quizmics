// Question Model Class

package com.quizapp.model;

public class Question {
    private int questionId;
    private int quizId;
    private String questionText;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctAnswer;

    public Question(int questionId, int quizId, String questionText, String option1, String option2, String option3, String option4, String correctAnswer) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
    }

    // Getters and setters
}