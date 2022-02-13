package ru.geekbrains.akaramanov.chatappjfx.service;

import java.io.Closeable;

public interface AuthService extends Closeable {
    String getNickByLoginAndPassword(String login, String password);
}
