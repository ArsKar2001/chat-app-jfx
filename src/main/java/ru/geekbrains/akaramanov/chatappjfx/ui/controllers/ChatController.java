package ru.geekbrains.akaramanov.chatappjfx.ui.controllers;

import javafx.application.Platform;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import ru.geekbrains.akaramanov.chatappjfx.ChatCommand;
import ru.geekbrains.akaramanov.chatappjfx.client.ChatClient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static javafx.geometry.NodeOrientation.*;

public class ChatController {
    public static final String STYLE_CSS = "ui/css/style.css";
    public static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private final ChatClient client;

    @FXML
    public VBox vbChatMessages;
    @FXML
    public HBox authBox;
    @FXML
    public TextField tfLogin;
    @FXML
    public TextField tfPassword;
    @FXML
    public Button btnAuth;
    @FXML
    public ListView<String> clientList;
    @FXML
    public BorderPane chatPane;
    @FXML
    private Button btnSendMessage;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField tfMessage;

    @FXML
    void initialize() {
        btnAuth.disableProperty()
                        .bind(tfLogin.textProperty().isEmpty().or(tfPassword.textProperty().isEmpty()));
        btnSendMessage.disableProperty()
                .bind(tfMessage.textProperty().isEmpty());
        // Установка вертикального скрола в нижнем положении относительно высоты окна.
        vbChatMessages.heightProperty()
                .addListener(observable -> scrollPane.setVvalue(1D));
        chatPane.setVisible(client.getAuthSuccess());
    }

    public ChatController() {
        client = new ChatClient(this);
    }

    public void addMessage(String message, NodeOrientation orientation) {
        Platform.runLater(() -> vbChatMessages.getChildren().add(new MessagePane(message, orientation)));
    }

    public void addMessage(String message) {
        Platform.runLater(() -> vbChatMessages.getChildren().add(new MessagePane(message)));
    }

    public void setAuth(boolean isSuccess) {
        authBox.setVisible(!isSuccess);
        chatPane.setVisible(isSuccess);
    }

    public void btnSendMessageClick(MouseEvent event) {
        String text = tfMessage.getText().trim();
        tfMessage.setText("");
        if (!text.equals("")) {
            addMessage(text, RIGHT_TO_LEFT);
            client.sendMessage(text);
        }
        tfMessage.requestFocus();
    }

    public void tfMessageKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String text = tfMessage.getText().trim();
            tfMessage.setText("");
            if (!text.equals("")) {
                addMessage(text, RIGHT_TO_LEFT);
                client.sendMessage(text);
            }
            tfMessage.requestFocus();
        }
    }

    public void btnAuthClick(MouseEvent event) {
        client.sendMessage("%s %s %s".formatted(ChatCommand.AUTH, tfLogin.getText(), tfPassword.getText()));
    }

    public void clientListSelect(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            String message = tfMessage.getText();
            tfMessage.setText("");
            String nick = clientList.getSelectionModel().getSelectedItem();
            String text = "%s %s %s".formatted(ChatCommand.WRITE, nick, message);
            client.sendMessage(text);
        }
    }

    public void sendAlert(String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            new Alert(type, message, ButtonType.OK).showAndWait();
        });
    }

    public void updateClientList(String[] clients) {
        Platform.runLater(() -> {
            clientList.getItems().clear();
            clientList.getItems().addAll(clients);
        });
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
            Label l2 = new Label(LocalTime.now().format(HH_MM));

            l1.getStyleClass().add("message-box-text");
            l2.getStyleClass().add("message-box-time");
            return getChildren().addAll(l1, l2);
        }

        public String getMessage() {
            return message;
        }
    }
}
