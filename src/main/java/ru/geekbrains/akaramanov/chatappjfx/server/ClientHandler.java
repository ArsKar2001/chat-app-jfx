package ru.geekbrains.akaramanov.chatappjfx.server;

import ru.geekbrains.akaramanov.chatappjfx.ChatCommand;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;

import static ru.geekbrains.akaramanov.chatappjfx.ChatCommand.*;

public class ClientHandler {
    private final Socket socket;
    private final ChatServer chatServer;
    private final DataInputStream in;
    private final DataOutputStream out;

    private String nick;

    public ClientHandler(Socket socket, ChatServer chatServer) throws IOException {
        this.nick = "";
        this.socket = socket;
        this.chatServer = chatServer;
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        new Thread(() -> {
            try (ClientSender receiver = new ClientSender(this)) {
                receiver.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }

    public String getNick() {
        return nick;
    }

    class ClientSender implements Closeable {
        private final ClientHandler client;

        public ClientSender(ClientHandler client) {
            this.client = client;
        }

        @Override
        public void close() throws IOException {
            socket.close();
            in.close();
            out.close();
            chatServer.unsubscribe(client);
            System.out.println("Клиент отключился!");
        }

        public void start() throws IOException {
            authenticate();
            readMessage();
        }

        private void logout() throws IOException {
            chatServer.unsubscribe(client);
            authenticate();
        }

        private void readMessage() throws IOException {
            while (true) {
                String message = in.readUTF();
                if (isCommand(message)) {
                    try {
                        ChatCommand command = getCommand(message);
                        if (command == END) {
                            sendMessage("Вы отключились от сервера!");
                            break;
                        }
                        if (command == WRITE) {
                            Matcher matcher = WRITE.getPattern().matcher(message);
                            if (matcher.find(0)) {
                                String[] ss = message.split("\\s");
                                String nick = ss[1];
                                String mes = matcher.replaceAll("");
                                chatServer.sendMessageByNick(nick, mes);
                            }
                            continue;
                        }
                    } catch (IllegalArgumentException e) {
                        sendMessage("Комманда введена неправильно!");
                        continue;
                    }
                }
                chatServer.broadcast(message, client);
            }
            logout();
        }

        private void authenticate() throws IOException {
            while (true) {
                String message = in.readUTF();
                Matcher matcher = AUTH.getPattern().matcher(message);
                if (matcher.find(0)) {
                    String command = message.substring(matcher.start(), matcher.end());
                    String[] ss = command.split("\\s");
                    String login = ss[1];
                    String password = ss[2];

                    String nick = chatServer.getNick(login, password);
                    if (nick != null) {
                        if (chatServer.isNickBusy(nick)) {
                            sendMessage("Пользователь уже авторизован!");
                            continue;
                        }
                        client.nick = nick;
                        sendMessage(AUTH_OK + " " + nick);
                        chatServer.broadcast("Пользователь " + nick + " зашел в чат!");
                        chatServer.subscribe(client);

                        break;
                    } else {
                        sendMessage("Неверные лолин и пароль!");
                    }
                }
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientHandler that = (ClientHandler) o;

        return nick.equals(that.nick);
    }

    @Override
    public int hashCode() {
        return nick.hashCode();
    }
}
