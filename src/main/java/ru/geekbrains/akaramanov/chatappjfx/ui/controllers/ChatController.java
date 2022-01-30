package ru.geekbrains.akaramanov.chatappjfx.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ru.geekbrains.akaramanov.chatappjfx.client.ChatClient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static javafx.geometry.NodeOrientation.*;

public class ChatController {
    public static final String STYLE_CSS = "ui/css/style.css";
    public static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ChatClient client;

    @FXML
    public VBox vbChatMessages;

    @FXML
    private Button btnSendMessage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField tfMessage;

    public ChatController() {
        client = new ChatClient(this);
    }

    @FXML
    void initialize() {

        btnSendMessage.disableProperty()
                .bind(tfMessage.textProperty().isEmpty());

        // Установка вертикального скрола в нижнем положении относительно высоты окна.
        vbChatMessages.heightProperty()
                .addListener(observable -> scrollPane.setVvalue(1D));

        tfMessage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String text = tfMessage.getText().trim();
                tfMessage.setText("");
                if (!text.equals("")) {
                    addMessage(text, RIGHT_TO_LEFT);
                    client.sendMessage(text);
                }
                tfMessage.requestFocus();
            }
        });

        btnSendMessage.setOnAction(actionEvent -> {
            String text = tfMessage.getText().trim();
            tfMessage.setText("");
            if (!text.equals("")) {
                addMessage(text, RIGHT_TO_LEFT);
                client.sendMessage(text);
            }
            tfMessage.requestFocus();
        });
    }

    public void addMessage(String message, NodeOrientation orientation) {
        Platform.runLater(() -> vbChatMessages.getChildren().add(new MessagePane(message, orientation)));
    }

    public void addMessage(String message) {
        Platform.runLater(() -> vbChatMessages.getChildren().add(new MessagePane(message)));
    }

    private static class MessagePane extends Pane {
        private final MessageBox box;

        {
            this.getStylesheets().add(STYLE_CSS);
        }

        private MessagePane(String message) {
            add(this.box = new MessageBox(message));
            this.setNodeOrientation(LEFT_TO_RIGHT);
        }

        private MessagePane(String message, NodeOrientation orientation) {
            add(this.box = new MessageBox(message));
            this.setNodeOrientation(orientation);
        }

        public boolean add(MessageBox box) {
            return getChildren().add(box);
        }

    }

    private static class MessageBox extends VBox {
        private final String message;

        {
            this.getStylesheets().add(STYLE_CSS);
            this.getStyleClass().add("message-box");
            this.setPadding(new Insets(5));
        }

        public MessageBox(String message) {
            add(this.message = message);
        }

        public boolean add(String message) {
            Label l1 = new Label(message);
            Label l2 = new Label(LocalTime.now().format(HH_MM_SS));

            l1.getStyleClass().add("message-box-text");
            l2.getStyleClass().add("message-box-time");

            return getChildren().addAll(l1, l2);
        }

        public String getMessage() {
            return message;
        }
    }
}
