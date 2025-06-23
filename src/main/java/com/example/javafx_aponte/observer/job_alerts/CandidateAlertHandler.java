package com.example.javafx_aponte.observer.job_alerts;

import com.example.javafx_aponte.models.JobVacancy;
import com.example.javafx_aponte.models.Profile;
import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.repository.JobVacancyRepository;
import com.example.javafx_aponte.repository.ProfileRepository;
import com.example.javafx_aponte.repository.UserRepository;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CandidateAlertHandler implements JobAlertObserver {
    private final ProfileRepository profileRepo;
    private final UserRepository userRepo; // Para obtener detalles completos del usuario

    public CandidateAlertHandler(ProfileRepository profileRepo, UserRepository userRepo) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void onNewJobPosted(JobVacancyEvent event) {
        JobVacancy vacancy = event.getVacancy();
        List<User> matchingCandidates = findMatchingCandidates(vacancy.getJobRequirements());

        matchingCandidates.forEach(candidate -> {
            String message = buildAlertMessage(vacancy, candidate);
            showJavaFXAlert(candidate, message);
        });
    }

    private List<User> findMatchingCandidates(List<String> requiredSkills) {
        // 1. Buscar perfiles que tengan TODAS las habilidades requeridas
        List<Profile> matchingProfiles = profileRepo.findProfileBySkills(requiredSkills);

        // 2. Obtener usuarios completos (con sus perfiles ya cargados)
        return matchingProfiles.stream()
                .map(profile -> userRepo.findUserById(profile.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private String buildAlertMessage(JobVacancy vacancy, User user) {
        return String.format("""
            🚀 ¡Nueva vacante para ti, %s!
            -------------------------------
            Puesto: %s
            Empresa: %s
            Habilidades requeridas: %s
            Fecha: %s
            """,
                user.getUsername(),
                vacancy.getTitle(),
                vacancy.getCompany().getNameCompany(),
                String.join(", ", vacancy.getJobRequirements()),
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
    }

    //Revisar metodos de dislpay
    private void showJavaFXAlert(User user, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nueva oportunidad laboral");
            alert.setHeaderText("Vacante coincidente con tu perfil");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
