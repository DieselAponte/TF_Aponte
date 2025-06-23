package com.example.javafx_aponte.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }



    public static void cambiarVista(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(SceneManager.class.getResource(fxmlPath)));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1100, 650);
            primaryStage.setScene(scene);
            primaryStage.setTitle(titulo);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error al cambiar de vista: " + e.getMessage());
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("No se ha inicializado el primaryStage o la ruta FXML es incorrecta.");
            e.printStackTrace();
        }
    }


}