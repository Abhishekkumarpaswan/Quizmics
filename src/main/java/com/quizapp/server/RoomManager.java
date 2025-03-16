package com.quizapp.server;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private final int roomId; // ID of the room
    private final List<ClientHandler> clients; // List of clients in the room

    public RoomManager(int roomId) {
        this.roomId = roomId;
        this.clients = new ArrayList<>();
    }

    /**
     * Adds a client to the room.
     *
     * @param client The client to add.
     */
    public void addClient(ClientHandler client) {
        clients.add(client);
        System.out.println("Client " + client.getUserId() + " joined room " + roomId);
        broadcast("CLIENT_JOINED:" + client.getUserId()); // Notify all clients about the new participant
    }

    /**
     * Removes a client from the room.
     *
     * @param client The client to remove.
     */
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client " + client.getUserId() + " left room " + roomId);
        broadcast("CLIENT_LEFT:" + client.getUserId()); // Notify all clients about the departure
    }

    /**
     * Broadcasts a message to all clients in the room.
     *
     * @param message The message to broadcast.
     */
    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.println("Failed to send message to client " + client.getUserId() + ": " + e.getMessage());
                removeClient(client); // Remove the client if communication fails
            }
        }
    }

    /**
     * Gets the number of clients in the room.
     *
     * @return The number of clients.
     */
    public int getClientCount() {
        return clients.size();
    }

    /**
     * Gets the room ID.
     *
     * @return The room ID.
     */
    public int getRoomId() {
        return roomId;
    }

    /**
     * Checks if the room is empty.
     *
     * @return True if the room is empty, false otherwise.
     */
    public boolean isEmpty() {
        return clients.isEmpty();
    }
}