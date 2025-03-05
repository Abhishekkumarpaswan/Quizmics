package com.quizapp.server;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private int roomId;
    private List<ClientHandler> clients;

    public RoomManager(int roomId) {
        this.roomId = roomId;
        this.clients = new ArrayList<>();
    }

    public void addClient(ClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}