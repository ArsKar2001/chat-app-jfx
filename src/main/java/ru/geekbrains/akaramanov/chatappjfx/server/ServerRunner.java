package ru.geekbrains.akaramanov.chatappjfx.server;

import java.io.IOException;

public class ServerRunner {
    public static void main(String[] args) {
        try (ChatServer server = new ChatServer()) {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
