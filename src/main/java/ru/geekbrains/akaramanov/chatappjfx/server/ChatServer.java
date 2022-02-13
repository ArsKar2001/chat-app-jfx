package ru.geekbrains.akaramanov.chatappjfx.server;

import ru.geekbrains.akaramanov.chatappjfx.ChatCommand;
import ru.geekbrains.akaramanov.chatappjfx.service.AuthService;
import ru.geekbrains.akaramanov.chatappjfx.service.DBAuthService;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatServer implements Closeable {
    private final Map<String, ServerHandler> handlers;
    private final AuthService authService;

    public ChatServer() {
        handlers = new HashMap<>();
        authService = new DBAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                System.out.println("Ждем подключения клиента...");
                Socket socket = serverSocket.accept();
                new ServerHandler(socket, this);
                System.out.println("Клиент подключился!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isNickBusy(String nick) {
        return handlers.containsKey(nick);
    }

    public void subscribe(ServerHandler handler) {
        System.out.println("Добавлен клиент: " + handlers.put(handler.getNick(), handler));
    }

    public void unsubscribe(ServerHandler client) {
        handlers.remove(client.getNick());
    }

    public void sendMessageByNick(String nick, String message) throws IOException {
        ServerHandler clientTo = handlers.get(nick);
        if (clientTo != null) {
            clientTo.sendMessage("От " + nick + ": " + message);
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public String getNick(String login, String password) {
        return authService.getNickByLoginAndPassword(login, password);
    }

    public void broadcastClientList() throws IOException {
        String message = handlers.values().stream()
                .map(ServerHandler::getNick)
                .collect(Collectors.joining(" ", ChatCommand.CLIENTS + " ", ""));
        broadcast(message);
    }

    public void broadcast(String message, ServerHandler current) throws IOException {
        for (ServerHandler c : handlers.values())
            if (!c.equals(current)) {
                String nick = current.getNick();
                c.sendMessage("От %s:%n%s".formatted(nick, message));
            }
    }

    public void broadcast(String message) throws IOException {
        for (ServerHandler c : handlers.values())
            c.sendMessage(message);
    }

    @Override
    public void close() throws IOException {
        authService.close();
    }
}
