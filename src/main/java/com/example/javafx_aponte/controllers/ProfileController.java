package com.example.javafx_aponte.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProfileController {

    public void mostrarVentanaExperiencia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("añadir-experiencia-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Añadir experiencia");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // bloquea hasta que se cierre
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
