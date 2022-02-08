package ru.geekbrains.akaramanov.chatappjfx.service;

import java.util.Optional;

public interface AuthService {
    Optional<String> getNickByLoginAndPassword(String login, String password);
}
