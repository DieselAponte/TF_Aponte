package com.example.javafx_aponte;

import com.example.javafx_aponte.controllers.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setPrimaryStage(stage);
        SceneManager.cambiarVista("/com/example/javafx_aponte/login-view.fxml", "Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
