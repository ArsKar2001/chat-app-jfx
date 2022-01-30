package ru.geekbrains.akaramanov.chatappjfx.server;

import ru.geekbrains.akaramanov.chatappjfx.service.AuthService;
import ru.geekbrains.akaramanov.chatappjfx.service.InMemoryAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChatServer {
    private final Map<String, ClientHandler> clients;
    private final AuthService authService;

    public ChatServer() {
        clients = new HashMap<>();
        authService = new InMemoryAuthService();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                System.out.println("Ждем подклбчения клиента...");
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, this);
                System.out.println("Клиент подключился!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isNickBusy(String nick) {
        return clients.containsKey(nick);
    }

    public void subscribe(ClientHandler client) {
        System.out.println("Добавлен клиент: " + clients.put(client.getNick(), client));
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
    }

    public void sendMessageByNick(String nick, String message) throws IOException {
        if (isNickBusy(nick)) {
            clients.get(nick).sendMessage(message);
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public String getNick(String login, String password) {
        return authService.getNickByLoginAndPassword(login, password)
                .orElse(null);
    }

    public void broadcast(String message, ClientHandler current) throws IOException {
        for (ClientHandler c : clients.values())
            if (!c.equals(current))
                c.sendMessage(message);
    }

    public void broadcast(String message) throws IOException {
        for (ClientHandler c : clients.values())
                c.sendMessage(message);
    }
}
