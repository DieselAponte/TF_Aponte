package com.example.javafx_aponte.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    // Credenciales temporales (esto se reemplazará con repository)
    private static final String USUARIO_VALIDO = "admin";
    private static final String CONTRASENA_VALIDA = "12345";

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private CheckBox checkRecordar;

    @FXML
    private Button botonIngresar;

    @FXML
    private Button botonCrearCuenta;

    @FXML
    private void initialize() {
        // Configuración inicial si es necesaria
        botonIngresar.setDefaultButton(true); // Permite usar Enter para iniciar sesión
    }

    @FXML
    private void mostrarCreateAccount() {
        SceneManager.cambiarVista("/org/example/bolsalaboralapp/create-account-view.fxml", "Crear Cuenta");
    }

    @FXML
    private void mostrarMainMenu() {
        // Validar campos vacíos
        if (campoUsuario.getText().isEmpty() || campoContrasena.getText().isEmpty()) {
            mostrarAlerta("Error de ingreso", "Por favor complete todos los campos");
            return;
        }

        // Validación temporal de credenciales
        if (validarCredenciales(campoUsuario.getText(), campoContrasena.getText())) {
            SceneManager.cambiarVista("/org/example/bolsalaboralapp/main-view.fxml", "Bolsa Laboral - Menú Principal");
        } else {
            mostrarAlerta("Credenciales incorrectas", "Usuario o contraseña incorrectos");
            campoContrasena.clear();
        }
    }

    private boolean validarCredenciales(String usuario, String contrasena) {
        // Validación simple (esto será reemplazado por repository)
        return usuario.equals(USUARIO_VALIDO) && contrasena.equals(CONTRASENA_VALIDA);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
