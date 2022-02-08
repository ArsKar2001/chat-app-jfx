package ru.geekbrains.akaramanov.chatappjfx.server;

public class ServerRunner {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
}
