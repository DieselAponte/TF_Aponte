package com.example.javafx_aponte.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AñadirExperienciaController {

    @FXML
    private TextField campoPuesto;
    @FXML private TextField campoEmpresa;
    @FXML private DatePicker campoFechaInicio;
    @FXML private DatePicker campoFechaFin;
    @FXML private TextArea campoDescripcion;

    @FXML
    private void guardarExperiencia() {
        String puesto = campoPuesto.getText();
        String empresa = campoEmpresa.getText();
        String inicio = campoFechaInicio.getValue() != null ? campoFechaInicio.getValue().toString() : "";
        String fin = campoFechaFin.getValue() != null ? campoFechaFin.getValue().toString() : "";
        String descripcion = campoDescripcion.getText();

        System.out.println("Puesto: " + puesto);
        System.out.println("Empresa: " + empresa);
        System.out.println("Inicio: " + inicio);
        System.out.println("Fin: " + fin);
        System.out.println("Descripción: " + descripcion);

        // Aquí podrías guardar en BD o actualizar la vista principal

        cerrarVentana();
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) campoPuesto.getScene().getWindow();
        stage.close();
    }
}