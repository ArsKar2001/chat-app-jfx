package ru.geekbrains.akaramanov.chatappjfx.client;

import ru.geekbrains.akaramanov.chatappjfx.ui.controllers.ChatController;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;

import static ru.geekbrains.akaramanov.chatappjfx.ChatCommand.*;

public class ChatClient {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final ChatController controller;

    public ChatClient(ChatController controller) {
        this.controller = controller;
        openConnection();

        Thread thread = new Thread(() -> {
            try (ClientReceiver sender = new ClientReceiver()) {
                sender.start();
            } catch (IOException e) {
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
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientReceiver implements Closeable {

        @Override
        public void close() throws IOException {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        }

        public void start() {
            authOK();
            send();
        }

        private void authOK() {
            try {
                while (true) {
                    String message = in.readUTF();
                    Matcher matcher = AUTH_OK.getPattern().matcher(message);
                    if (matcher.find(0)) {
                        String command = message.substring(matcher.start(), matcher.end());
                        String nick = command.split("\\s")[1];
                        controller.addMessage("Успешная авторизация под ником: " + nick);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void send() {
            try {
                String message = "";
                while (!END.toString().equals(message)) {
                    message = in.readUTF();
                    controller.addMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
