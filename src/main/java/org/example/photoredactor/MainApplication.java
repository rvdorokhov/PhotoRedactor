package org.example.photoredactor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("test.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Contra");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setMinWidth(500);
        stage.setMinHeight(250);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}