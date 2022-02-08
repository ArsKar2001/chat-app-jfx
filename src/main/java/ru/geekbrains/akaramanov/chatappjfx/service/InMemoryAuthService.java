package ru.geekbrains.akaramanov.chatappjfx.service;

import java.util.*;

public class InMemoryAuthService implements AuthService {

    private final Set<UserData> users;

    public InMemoryAuthService() {
        users = new HashSet<>();

        for (int i = 0; i < 10; i++) {
            users.add(new UserData(
                    "login" + i,
                    "password" + i,
                    "nick" + i
            ));
        }
    }

    @Override
    public Optional<String> getNickByLoginAndPassword(String login, String password) {
        return users.stream()
                .filter(data -> data.login.equals(login) && data.password.equals(password))
                .map(UserData::getNick)
                .findFirst();
    }

    private static class UserData {
        private final String login;
        private final String password;
        private final String nick;

        public UserData(String login, String password, String nick) {
            this.login = login;
            this.password = password;
            this.nick = nick;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserData userData = (UserData) o;

            if (!Objects.equals(login, userData.login)) return false;
            if (!Objects.equals(password, userData.password)) return false;
            return Objects.equals(nick, userData.nick);
        }

        @Override
        public int hashCode() {
            int result = login != null ? login.hashCode() : 0;
            result = 31 * result + (password != null ? password.hashCode() : 0);
            result = 31 * result + (nick != null ? nick.hashCode() : 0);
            return result;
        }
    }
}
