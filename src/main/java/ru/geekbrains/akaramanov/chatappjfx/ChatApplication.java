package ru.geekbrains.akaramanov.chatappjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApplication extends Application {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 400;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/fxml/chat-view.fxml"));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
        stage.setScene(scene);
    }
}
