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
import com.example.javafx_aponte.models.JobVacancies;
import com.example.javafx_aponte.models.Session;
import com.example.javafx_aponte.services.JobService;
import com.example.javafx_aponte.services.NotificationService;
import com.example.javafx_aponte.services.PostulationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.example.javafx_aponte.repository.*;
public class MainPageController {

    @FXML private ToggleGroup tipoGroup;
    @FXML private ToggleGroup experienciaGroup;
    @FXML private ToggleGroup sueldoGroup;
    @FXML private TextField searchBar;
    @FXML private Button btnNotifications, btnMessages, btnProfile;
    @FXML private Label notificationBadge;
    @FXML private VBox jobListContainer, jobDetailPanel;
    @FXML private ListView<JobVacancies> jobListView;
    @FXML private Label jobTitleLabel, jobCompanyLabel, jobLocationLabel, jobDetailsLabel;
    @FXML private Button btnPostularme;

    private JobVacancies selectedJob;
    private final IntegerProperty notificationCount = new SimpleIntegerProperty(0);
    //Servicios:
    private final JobService jobService;
    private final PostulationService postulationService;
    private final NotificationService notificationService;


    public MainPageController(JobService jobService, PostulationService postulationService, NotificationService notificationService) {
        this.jobService = jobService;
        this.postulationService =  postulationService;
        this.notificationService = notificationService;
    }

    @FXML
    public void initialize() {
        if (btnNotifications == null || notificationBadge == null) {
            throw new IllegalStateException("""
                Error: Elementos FXML no inyectados. Verifica que:
                1. Los fx:id en el FXML coincidan exactamente
                2. El FXML esté en la ruta correcta
                3. Los nombres de paquetes sean correctos""");
        }

        btnNotifications.setOnAction(e -> openNotificationsWindow());
        setupNotifications();
        setupJobList();
        setupFilters();
        setupSearch();
    }

    private void setupNotifications() {
        // Usar el método correcto del servicio
        notificationCount.set(notificationService.getUnreadNotificationCount(Session.getCurrentUser()));

        notificationCount.addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > 0) {
                notificationBadge.setText(String.valueOf(newVal));
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
        });

        // Actualizar inicialmente
        notificationCount.set(notificationService.getUnreadNotificationCount(Session.getCurrentUser()));
    }

    private void setupJobList() {
        jobListView.setCellFactory(lv -> new ListCell<JobVacancies>() {
            @Override
            protected void updateItem(JobVacancies item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("%s\n%s", item.getTitle(), item.getCompany().getNameCompany()));
            }
        });

        jobListView.setOnMouseClicked(this::onTrabajoSeleccionado);
        btnPostularme.setOnAction(event -> postularse());
        cargarTrabajos(jobService.getAllJobs());
    }

    private void openNotificationsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafx_aponte/views/NotificationsView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Notificaciones");
            stage.setScene(new Scene(root, 400, 500));
            stage.show();

            // Resetear contador después de abrir
            notificationCount.set(0);
            notificationService.markAllNotificationsAsRead(Session.getCurrentUser());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error al abrir notificaciones");
        }
    }


    private void setupFilters() {
        tipoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        experienciaGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        sueldoGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
    }


    private void setupSearch() {
        searchBar.setOnAction(e -> aplicarFiltros());
    }

    private void cargarTrabajos(List<JobVacancies> trabajos) {
        jobListView.getItems().setAll(trabajos);
    }

    private void onTrabajoSeleccionado(MouseEvent event) {
        selectedJob = jobListView.getSelectionModel().getSelectedItem();
        if (selectedJob != null) {
            jobDetailPanel.setVisible(true);
            jobTitleLabel.setText(selectedJob.getTitle());
            jobCompanyLabel.setText(selectedJob.getCompany().getNameCompany());
            jobLocationLabel.setText(selectedJob.getCompany().getCompanyAddress());
            jobDetailsLabel.setText(selectedJob.getDescription());
        }
    }

    private void postularse() {
        if (Session.getCurrentUser() == null) {
            showAlert("Debe iniciar sesión para postular.");
            return;
        }

        if (selectedJob == null) {
            showAlert("Seleccione un trabajo primero");
            return;
        }

        try {
            Postulation postulation = postulationService.applyForJob(Session.getCurrentUser(), selectedJob.getId());
            showAlert("¡Postulación enviada correctamente!");
        } catch (IllegalStateException e) {
            showAlert(e.getMessage()); // "Ya te has postulado a esta vacante"
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage()); // "Vacante no existe"
        }
    }

    private void aplicarFiltros() {
        String tipo = getSelectedText(tipoGroup);
        String experiencia = getSelectedText(experienciaGroup);
        String sueldoTexto = getSelectedText(sueldoGroup);
        String busqueda = searchBar.getText().trim().toLowerCase();

        double sueldo = sueldoTexto != null ? Double.parseDouble(sueldoTexto.replaceAll("[^\\d]", "")) : 0;
        double sueldoMax = sueldo + 1000;

        List<JobVacancies> filtrados = jobService.searchJobs(
                busqueda.isEmpty() ? null : busqueda,
                tipo,
                sueldo,
                sueldoMax
        );

        cargarTrabajos(filtrados);
    }

    private String getSelectedText(ToggleGroup group) {
        Toggle toggle = group.getSelectedToggle();
        return toggle != null ? ((RadioButton) toggle).getText() : null;
    }

    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}