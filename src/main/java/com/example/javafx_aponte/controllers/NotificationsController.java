package com.example.javafx_aponte.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class NotificationsController {

    @FXML
    private ListView<String> listaNotificaciones;

    private MainPageController mainController;

    public void setMainController(MainPageController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void mostrarMainMenu() {
        SceneManager.cambiarVista("/org/example/bolsalaboralapp/main-view.fxml", "Bolsa Laboral - Menú Principal");
    }

    @FXML
    public void initialize() {
        listaNotificaciones.getItems().addAll(
                "Tu postulación a 'Analista Junior' ha sido recibida.",
                "Nueva oferta laboral: Desarrollador Frontend - Remoto",
                "Recuerda actualizar tu currículum este mes.",
                "3 empresas han visitado tu perfil esta semana."
        );
    }

    @FXML
    private void mostrarPerfil() {
        SceneManager.cambiarVista("perfil-view.fxml", "Perfil de Usuario");
    }

    @FXML
    private void mostrarNotificaciones() {
        SceneManager.cambiarVista("notificaciones-view.fxml", "Notificaciones");
    }

    @FXML
    private void mostrarPostulaciones() {
        SceneManager.cambiarVista("postulaciones-view.fxml", "Mis Postulaciones");
    }

}
