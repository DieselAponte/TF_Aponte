package com.example.javafx_aponte.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateAccountController {

    @FXML
    TextField txtNuevoUsuario;

    @FXML
    private void volverALogin() {
        SceneManager.cambiarVista("/org/example/bolsalaboralapp/login-view.fxml", "Login");
    }

}