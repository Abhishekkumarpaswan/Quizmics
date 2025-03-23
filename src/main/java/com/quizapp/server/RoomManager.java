package com.quizapp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RoomManager {

    private static final Logger logger = Logger.getLogger(RoomManager.class.getName());
    private int roomId;
    private List<ClientHandler> clients = new ArrayList<>();

    public RoomManager(int roomId) {
        this.roomId = roomId;
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
        logger.info("Client added to room " + roomId + ": " + client.getUserId());
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        logger.info("Client removed from room " + roomId + ": " + client.getUserId());
    }

    public void broadcast(String message) {
        logger.info("Broadcasting message to room " + roomId + ": " + message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}
