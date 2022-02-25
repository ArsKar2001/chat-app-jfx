package ru.geekbrains.akaramanov.chatappjfx.client;

import javafx.scene.control.Alert.AlertType;
import ru.geekbrains.akaramanov.chatappjfx.ui.controllers.ChatController;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static ru.geekbrains.akaramanov.chatappjfx.ChatCommand.*;

public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Boolean authSuccess;

    private final ChatController controller;

    public ChatClient(ChatController controller) {
        this.controller = controller;
        authSuccess = false;

        openConnection();

        Thread thread = new Thread(() -> {
            try (ClientReceiver sender = new ClientReceiver()) {
                sender.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void openConnection() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            controller.sendAlert("Не удалось подключиться к серверу: " + e.getMessage(), AlertType.ERROR);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean getAuthSuccess() {
        return authSuccess;
    }

    class ClientReceiver implements Closeable {

        @Override
        public void close() throws IOException {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        }

        public void start() throws IOException {
            authOK();
            read();
        }

        private void authOK() throws IOException {
            while (true) {
                String message = in.readUTF();
                if (getCommand(message) == ERROR) {
                    String s = ERROR.getMatch(message).replaceAll("");
                    controller.sendAlert(s, AlertType.ERROR);
                    continue;
                }
                if (authSuccess = (getCommand(message) == AUTH_OK)) {
                    String nick = message.replaceAll(AUTH_OK + "\\s+", "");
                    controller.sendAlert("Успешная авторизация под ником: " + nick, AlertType.INFORMATION);
                    controller.setAuth(authSuccess = true);
                    break;
                }
            }
        }


        private void read() throws IOException {
            while (true) {
                String message;
                message = in.readUTF();
                if (getCommand(message) == END) {
                    break;
                }
                if (getCommand(message) == CLIENTS) {
                    String[] clients = CLIENTS.getMatch(message).replaceAll("").split("\\s");
                    controller.updateClientList(clients);
                    continue;
                }
                controller.addMessage(message);
            }
            logout();
        }

        private void logout() throws IOException {
            controller.setAuth(authSuccess = false);
        }
    }
}
