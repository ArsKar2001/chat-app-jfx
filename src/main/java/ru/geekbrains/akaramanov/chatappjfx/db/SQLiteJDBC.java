package ru.geekbrains.akaramanov.chatappjfx.db;

import ru.geekbrains.akaramanov.chatappjfx.models.User;

import java.io.Closeable;
import java.sql.*;

public class SQLiteJDBC implements Closeable {
    private Connection connection;
    protected Statement statement;

    public SQLiteJDBC() {
        connect();
    }

    public void insertUser(User user) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO users(login, password, nick) " +
                "VALUES (?, ?, ?)")) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getNick());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getNick(String login, String password) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT nick FROM users " +
                "WHERE login = ? AND password = ?")) {
            ps.setString(1, login);
            ps.setString(2, password);
            return ps.executeQuery().getString("nick");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:db/chatdb.db");
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
