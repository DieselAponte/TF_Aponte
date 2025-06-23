package com.example.javafx_aponte.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AñadirArchivoController {

    @FXML
    private Button btnSeleccionarArchivo;

    @FXML
    private Label lblNombreArchivo; // Corregí el nombre (antes tenía un typo)

    @FXML
    private ListView<File> listaArchivos;

    private Stage stage;
    private List<File> archivosSeleccionados = new ArrayList<>();
    private File ultimoDirectorio = null; // Para recordar la última ubicación

    @FXML
    public void initialize() {
        // Configurar el ListView
        listaArchivos.setCellFactory(lv -> new ListCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    @FXML
    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivos");

        // Configurar directorio inicial si existe uno previo
        if (ultimoDirectorio != null && ultimoDirectorio.exists()) {
            fileChooser.setInitialDirectory(ultimoDirectorio);
        }

        // Filtros para tipos de archivos (opcional)
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Documentos soportados", "*.pdf", "*.doc", "*.docx", "*.odt");
        fileChooser.getExtensionFilters().add(extFilter);

        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            archivosSeleccionados.addAll(files);
            listaArchivos.getItems().setAll(archivosSeleccionados);
            lblNombreArchivo.setText(files.size() + " archivo(s) seleccionado(s)");

            // Guardar la última ubicación para futuras selecciones
            ultimoDirectorio = files.get(0).getParentFile();
        }
    }

    @FXML
    private void aceptar() {
        cerrarVentana();
    }

    @FXML
    private void cancelar() {
        archivosSeleccionados.clear();
        cerrarVentana();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public List<File> getArchivosSeleccionados() {
        return new ArrayList<>(archivosSeleccionados); // Devolver copia
    }

    private void cerrarVentana() {
        if (stage != null) {
            stage.close();
        }
    }
}
