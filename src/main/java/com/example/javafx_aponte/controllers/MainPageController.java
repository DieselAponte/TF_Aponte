package com.example.javafx_aponte.controllers;

import com.example.javafx_aponte.models.Postulation;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.javafx_aponte.models.JobVacancy;
import com.example.javafx_aponte.models.Session;
import com.example.javafx_aponte.services.JobService;
import com.example.javafx_aponte.services.NotificationService;
import com.example.javafx_aponte.services.PostulationService;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainPageController {

    @FXML
    public void mostrarPerfil() {
        SceneManager.cambiarVista("perfil-view.fxml", "Perfil de Usuario");
    }

    @FXML
    public void mostrarNotificaciones() {
        SceneManager.cambiarVista("notificaciones-view.fxml", "Notificaciones");
    }

    @FXML
    public void mostrarPostulaciones() {
        SceneManager.cambiarVista("postulaciones-view.fxml", "Mis Postulaciones");
    }

    @FXML
    private void mostrarMainMenu() {
        SceneManager.cambiarVista("/org/example/bolsalaboralapp/main-view.fxml", "Bolsa Laboral - Menú Principal");
    }

    @FXML
    private void postularme() {
        // Lógica para postularse a un trabajo
        System.out.println("Postulación enviada");
    }

    @FXML
    public void mostrarVentanaAdjuntarArchivo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/bolsalaboralapp/adjuntar-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Adjuntar Archivos");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar mensaje de error
        }
    }

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