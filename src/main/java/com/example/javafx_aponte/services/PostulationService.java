package com.example.javafx_aponte.services;

import com.example.javafx_aponte.models.JobVacancies;
import com.example.javafx_aponte.models.Postulation;
import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.repository.JobVacancyRepository;
import com.example.javafx_aponte.repository.PostulationRepository;
import com.example.javafx_aponte.repository.UserRepository;
import com.example.javafx_aponte.util.PostulationStatus;

import java.time.LocalDate;
import java.util.List;

public class PostulationService {
    private final PostulationRepository postulationRepo;
    private final UserRepository userRepo;
    private final JobVacancyRepository jobRepo;

    public PostulationService(PostulationRepository postulationRepo, UserRepository userRepo, JobVacancyRepository jobRepo) {
        this.postulationRepo = postulationRepo;
        this.userRepo = userRepo;
        this.jobRepo = jobRepo;
    }

    public Postulation applyForJob(User user, int jobId) {
        // Validar que el usuario y la vacante existen
        User existingUser = userRepo.findUserById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        JobVacancies job = jobRepo.findJobVacancyById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Vacante no existe"));

        // Verificar si ya existe una postulación
        if (postulationRepo.existsPostulationByUserAndJobVacancy(user, jobId)) {
            throw new IllegalStateException("Ya te has postulado a esta vacante");
        }

        // Crear la postulación con la estructura correcta
        Postulation postulation = new Postulation(
                0,                  // ID se generará automáticamente
                existingUser,       // Objeto User completo
                job,                // Objeto JobVacancies completo
                PostulationStatus.EN_GESTION,
                LocalDate.now()
        );

        return postulationRepo.savePostulation(postulation);
    }

    public List<Postulation> getUserApplications(User user) {
        return postulationRepo.findPostulationByUser(user);
    }

    public List<Postulation> getJobApplications(int jobId) {
        return postulationRepo.findPostulationByJobVacancy(jobId);
    }

    public List<Postulation> getApplicationsByStatus(String status) {
        return postulationRepo.findPostulationByStatus(PostulationStatus.valueOf(status));
    }

    public void updateApplicationStatus(int postulationId, String status) {
        postulationRepo.updatePostulationStatus(postulationId, PostulationStatus.valueOf(status));
    }

    public void deleteApplication(int id) {
        postulationRepo.deletePostulation(id);
    }

    private void validateUserAndJob(User user, int jobId) {
        userRepo.findUserById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));
        jobRepo.findJobVacancyById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Vacante no existe"));
    }
}
