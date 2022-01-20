package ru.geekbrains.akaramanov.chatappjfx.ui.controllers;

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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatController {
    public static final String STYLE_CSS = "ui/css/style.css";
    public static final DateTimeFormatter HH_MM_SS = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    public VBox vbChatMessages;

    @FXML
    private Button btnSendMessage;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private TextField tfMessage;


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
                if (!text.isBlank()) {
                    vbChatMessages.getChildren().add(new MessagePane(text));
                }
            }
        });

        btnSendMessage.setOnAction(actionEvent -> {
            String text = tfMessage.getText().trim();
            tfMessage.setText("");
            if (!text.isBlank()) {
                 vbChatMessages.getChildren().add(new MessagePane(text));
            }
        });
    }

    private static class MessagePane extends Pane {
        private MessageBox box;

        {
            this.getStylesheets().add(STYLE_CSS);
            this.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }

        private MessagePane(MessageBox box) {
            add(this.box = box);
        }

        private MessagePane(String message) {
            add(this.box = new MessageBox(message));
        }

        public boolean add(MessageBox box) {
            return getChildren().add(box);
        }

        public MessageBox getBox() {
            return box;
        }

        public void setBox(MessageBox box) {
            this.box = box;
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
