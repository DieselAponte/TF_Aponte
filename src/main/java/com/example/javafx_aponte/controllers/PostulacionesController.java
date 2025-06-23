package com.example.javafx_aponte.controllers;


import com.example.javafx_aponte.models.Postulation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


public class PostulacionesController {
    @FXML
    private ListView<Postulation> listaPostulaciones;

    private MainPageController mainController;

    public void setMainController(MainPageController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void mostrarMainMenu() {
        SceneManager.cambiarVista("/org/example/bolsalaboralapp/main-view.fxml", "Bolsa Laboral - Menú Principal");
    }

//    @FXML
//    public void initialize() {
//        listaPostulaciones.getItems().addAll(
//                new Postulacion("Asistente de Marketing", "Creativa SAC", "Lima", "05/06/2025", "En proceso"),
//                new Postulacion("Programador Java", "SoftDev", "Remoto", "03/06/2025", "Rechazado"),
//                new Postulacion("Analista de Datos", "Dataconsult", "Arequipa", "01/06/2025", "Seleccionado")
//        );
//
//        listaPostulaciones.setCellFactory(new Callback<>() {
//            @Override
//            public ListCell<Postulacion> call(ListView<Postulacion> listView) {
//                return new ListCell<>() {
//                    @Override
//                    protected void updateItem(Postulacion item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if (empty || item == null) {
//                            setGraphic(null);
//                        } else {
//                            VBox vbox = new VBox(4);
//                            Label title = new Label(item.getTitulo());
//                            title.getStyleClass().add("app-title");
//
//                            Label empresa = new Label(item.getEmpresa() + " - " + item.getUbicacion());
//                            empresa.getStyleClass().add("app-company");
//
//                            Label fecha = new Label("Postulado el " + item.getFecha());
//                            fecha.getStyleClass().add("app-date");
//
//                            Label estado = new Label(item.getEstado());
//                            estado.getStyleClass().add("app-status");
//                            if (item.getEstado().equalsIgnoreCase("Rechazado"))
//                                estado.getStyleClass().add("rechazado");
//                            else if (item.getEstado().equalsIgnoreCase("En proceso"))
//                                estado.getStyleClass().add("en-proceso");
//
//                            vbox.getChildren().addAll(title, empresa, fecha, estado);
//                            setGraphic(vbox);
//                        }
//                    }
//                };
//            }
//        });
//    }

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