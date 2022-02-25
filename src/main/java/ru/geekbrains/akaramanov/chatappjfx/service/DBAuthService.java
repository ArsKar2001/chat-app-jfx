package ru.geekbrains.akaramanov.chatappjfx.service;

import ru.geekbrains.akaramanov.chatappjfx.db.SQLiteJDBC;

public class DBAuthService implements AuthService {
    private final SQLiteJDBC jdbc;

    public DBAuthService() {
        jdbc = new SQLiteJDBC();
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        return jdbc.getNick(login, password);
    }

    @Override
    public void close() {
        jdbc.close();
    }
}
