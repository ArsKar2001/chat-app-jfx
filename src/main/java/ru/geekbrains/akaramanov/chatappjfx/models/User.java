package ru.geekbrains.akaramanov.chatappjfx.models;

import java.util.Objects;

public class User {
    private final Integer id;
    private final String login;
    private final String password;
    private final String nick;

    public User(Builder builder) {
        id = builder.id;
        login = builder.login;
        password = builder.password;
        nick = builder.nick;
    }

    public static class Builder {
        private Integer id;
        private String login;
        private String password;
        private String nick;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }
        public Builder login(String login) {
            this.login = login;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        public Builder nick(String nick) {
            this.nick = nick;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(login, user.login)) return false;
        if (!Objects.equals(password, user.password)) return false;
        return Objects.equals(nick, user.nick);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (nick != null ? nick.hashCode() : 0);
        return result;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNick() {
        return nick;
    }
}
