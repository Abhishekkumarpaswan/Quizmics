package com.quizapp.model;

public class Room {
    private int roomId;
    private String roomName;
    private int quizId;
    private int createdBy;

    // Constructor
    public Room(int roomId, String roomName, int quizId, int createdBy) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.quizId = quizId;
        this.createdBy = createdBy;
    }

    // Getters
    public int getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getQuizId() {
        return quizId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    // Setters
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}