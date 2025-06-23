package com.example.javafx_aponte.observer.postulation;


import com.example.javafx_aponte.models.Postulation;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.example.javafx_aponte.util.PostulationStatus.*;

public class PostulationStatusNotifier implements PostulationObserver {
    @Override
    public void update(PostulationEvent event) {
        Postulation postulation = event.getPostulation();
        String message = switch (postulation.getStatus()) {
            case EN_GESTION -> generateInProcessMessage(postulation);
            case ENTREVISTA -> generateInterviewMessage(postulation);
            case ACEPTADO -> generateApprovedMessage(postulation);
            case RECHAZADO -> generateRejectedMessage(postulation);
            default -> null;
        };

        if (message != null) {
            showJavaFXAlert(message);
        }
    }

    private String generateInProcessMessage(Postulation postulation) {
        return String.format("""
            📌 Postulación en revisión
            --------------------------
            Vacante: %s
            Empresa: %s
            Fecha: %s
            Estado actual: EN GESTIÓN
            """,
                postulation.getJobVacancies().getTitle(),
                postulation.getJobVacancies().getCompany().getNameCompany(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }

    private String generateInterviewMessage(Postulation postulation) {
        return String.format("""
            🎉 ¡Convocatoria a entrevista!
            ------------------------------
            Vacante: %s
            Empresa: %s
            Próximos pasos: 
            - Recibirás un correo con la fecha
            - Prepárate para preguntas técnicas
            """,
                postulation.getJobVacancies().getTitle(),
                postulation.getJobVacancies().getCompany().getNameCompany()
        );
    }

    private String generateApprovedMessage(Postulation postulation) {
        return String.format("""
        ✅ ¡Postulación Aprobada!
        --------------------------
        Vacante: %s
        Empresa: %s
        Felicitaciones, has sido seleccionado para continuar con el proceso o para incorporarte al equipo.
        Revisa tu correo para más detalles.
        """,
                postulation.getJobVacancies().getTitle(),
                postulation.getJobVacancies().getCompany().getNameCompany()
        );
    }

    private String generateRejectedMessage(Postulation postulation) {
        return String.format("""
        ❌ Postulación Rechazada
        ------------------------
        Vacante: %s
        Empresa: %s
        Lamentablemente, no has sido seleccionado en esta oportunidad.
        ¡Gracias por postularte y te animamos a seguir intentándolo!
        """,
                postulation.getJobVacancies().getTitle(),
                postulation.getJobVacancies().getCompany().getNameCompany()
        );
    }



    //Revisar metodos - no creo que sea un buen display aaaaaaaaaaaaaaaaaaaa
    private void showJavaFXAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Actualización de Postulación");
            alert.setHeaderText("Nuevo estado de tu aplicación");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
