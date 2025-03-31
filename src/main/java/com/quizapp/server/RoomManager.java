package com.quizapp.server;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RoomManager {
    private static final Logger logger = Logger.getLogger(RoomManager.class.getName());
    private final int roomId;
    private final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    private static final Map<Integer, String> playerNames = new HashMap<>();
    private static final String PLAYER_LIST_PREFIX = "PLAYER_LIST:";

    public RoomManager(int roomId) {
        this.roomId = roomId;
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
        playerNames.put(client.getUserId(), client.getUsername());
        broadcastPlayerList();
        logger.info("Client " + client.getUserId() + " added to room " + roomId);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        playerNames.remove(client.getUserId());
        broadcastPlayerList();
        logger.info("Client " + client.getUserId() + " removed from room " + roomId);

        if (clients.isEmpty()) {
            QuizServer.removeRoomManager(roomId);
        }
    }

    public synchronized void broadcast(String message) {
        List<ClientHandler> toRemove = new ArrayList<>();

        synchronized (clients) {
            for (ClientHandler client : clients) {
                try {
                    client.sendMessage(message);
                } catch (Exception e) {
                    logger.warning("Failed to send to client " + client.getUserId() + ": " + e.getMessage());
                    toRemove.add(client);
                }
            }

            // Remove disconnected clients
            toRemove.forEach(this::removeClient);
        }
    }

    public synchronized void broadcastPlayerList() {
        String playerList = PLAYER_LIST_PREFIX + clients.stream()
                .map(ClientHandler::getUsername)
                .collect(Collectors.joining(","));
        broadcast(playerList);
    }

    public String getUsername(int userId) {
        return playerNames.get(userId);
    }
}
