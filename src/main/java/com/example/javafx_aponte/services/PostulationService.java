package com.example.javafx_aponte.services;

import com.example.javafx_aponte.models.Postulation;
import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.observer.postulation.PostulationEvent;
import com.example.javafx_aponte.observer.postulation.PostulationSubject;
import com.example.javafx_aponte.repository.PostulationRepository;
import com.example.javafx_aponte.repository.UserRepository;
import com.example.javafx_aponte.repository.JobVacancyRepository;
import com.example.javafx_aponte.util.PostulationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PostulationService extends PostulationSubject {
    private final PostulationRepository postulationRepo;
    private final UserRepository userRepo;
    private final JobVacancyRepository jobRepo;

    public PostulationService(PostulationRepository postulationRepo,
                              UserRepository userRepo,
                              JobVacancyRepository jobRepo) {
        this.postulationRepo = postulationRepo;
        this.userRepo        = userRepo;
        this.jobRepo         = jobRepo;
    }

    /**
     * Aplica a una vacante y notifica a los observers sobre la nueva postulación.
     */
    public Postulation applyForJob(User user, int jobId) {
        // 1) Validar existencia de usuario y vacante
        User existingUser = userRepo.findUserById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        var job = jobRepo.findJobVacancyById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Vacante no existe"));

        // 2) Verificar postulación previa
        if (postulationRepo.existsPostulationByUserAndJobVacancy(existingUser, jobId)) {
            throw new IllegalStateException("Ya te has postulado a esta vacante");
        }

        // 3) Crear la postulación
        Postulation postulation = new Postulation(
                0,
                existingUser,
                job,
                PostulationStatus.EN_GESTION,
                LocalDate.now()
        );
        Postulation saved = postulationRepo.savePostulation(postulation);

        // 4) Notificar creación de nueva postulación
        notifyObservers(new PostulationEvent(saved, null));
        return saved;
    }

    /**
     * Actualiza el estado de una postulación y notifica a los observers del cambio.
     */
    public void updateApplicationStatus(int postulationId, String newStatusStr) {
        // 1) Obtener postulación existente
        Postulation existing = postulationRepo.findPostulationById(postulationId)
                .orElseThrow(() -> new IllegalArgumentException("Postulación no existe"));

        // 2) Guardar estado anterior
        PostulationStatus previous = existing.getStatus();

        // 3) Actualizar en repositorio
        PostulationStatus newStatus = PostulationStatus.valueOf(newStatusStr);
        postulationRepo.updatePostulationStatus(postulationId, newStatus);

        // 4) Recuperar la instancia actualizada
        Postulation updated = postulationRepo.findPostulationById(postulationId)
                .orElseThrow();

        // 5) Notificar cambio de estado
        notifyObservers(new PostulationEvent(updated, previous));
    }

    /* Métodos de consulta y eliminación (sin cambios de Observer) */

    public List<Postulation> getUserApplications(User user) {
        return postulationRepo.findPostulationByUser(user);
    }

    public List<Postulation> getJobApplications(int jobId) {
        return postulationRepo.findPostulationByJobVacancy(jobId);
    }

    public List<Postulation> getApplicationsByStatus(String status) {
        return postulationRepo.findPostulationByStatus(PostulationStatus.valueOf(status));
    }

    public void deleteApplication(int id) {
        postulationRepo.deletePostulation(id);
    }
}
