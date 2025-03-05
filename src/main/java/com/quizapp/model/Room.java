//Room Model Class

package com.quizapp.model;

public class Room {
    private int roomId;
    private String roomName;
    private int quizId;
    private int createdBy;

    public Room(int roomId, String roomName, int quizId, int createdBy) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.quizId = quizId;
        this.createdBy = createdBy;
    }

    // Getters and setters
}