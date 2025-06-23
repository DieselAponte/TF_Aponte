package com.example.javafx_aponte;

import com.example.javafx_aponte.models.*;
import com.example.javafx_aponte.repository.*;
import com.example.javafx_aponte.services.*;
import com.example.javafx_aponte.util.PostulationStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class Main {
    private final UserService userService;
    private final JobService jobService;
    private final NotificationService notificationService;
    private final ProfileService profileService;
    private final PostulationService postulationService;
    private final CompanyService companyService;

    public Main() {
        UserService userServiceTemp       = null;
        JobService jobServiceTemp         = null;
        NotificationService notificationServiceTemp = null;
        ProfileService profileServiceTemp = null;
        PostulationService postulationServiceTemp = null;
        CompanyService companyServiceTemp = null;

        try (Connection connection = DataBaseConnection.getConnection()) {
            // Repositorios
            UserRepository        userRepo        = new ConcreteUserRepository(connection);
            CompanyRepository     companyRepo     = new ConcreteCompanyRepository(connection);
            JobVacancyRepository  jobRepo         = new ConcreteJobsVacancyRepository(connection);
            PostulationRepository postRepo        = new ConcretePostulationRepository(connection);
            NotificationRepository notifRepo      = new ConcreteNotificationRepository(connection);
            ProfileRepository     profileRepo     = new ConcreteProfileRepository(connection);
            SkillRepository       skillRepo       = new ConcreteSkillRepository(connection);

            // Servicios
            userServiceTemp       = new UserService(userRepo);
            companyServiceTemp    = new CompanyService(companyRepo);
            jobServiceTemp        = new JobService(jobRepo, companyRepo);
            notificationServiceTemp = new NotificationService(notifRepo);
            profileServiceTemp    = new ProfileService(profileRepo, userRepo, skillRepo);
            postulationServiceTemp = new PostulationService(postRepo, userRepo, jobRepo);

            // Insertar datos de prueba
            insertTestData(
                    userServiceTemp,
                    companyServiceTemp,
                    jobServiceTemp,
                    profileServiceTemp,
                    postulationServiceTemp,
                    notificationServiceTemp
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error de conexión a la base de datos: " + e.getMessage());
            System.exit(1);
        }

        this.userService         = userServiceTemp;
        this.companyService      = companyServiceTemp;
        this.jobService          = jobServiceTemp;
        this.notificationService = notificationServiceTemp;
        this.profileService      = profileServiceTemp;
        this.postulationService  = postulationServiceTemp;
    }

    private void insertTestData(
            UserService userService,
            CompanyService companyService,
            JobService jobService,
            ProfileService profileService,
            PostulationService postulationService,
            NotificationService notificationService
    ) {
        // 1. Registrar usuarios
        User applicant1 = userService.registerUser(
                new User(0, "juan_perez", "juan@email.com", "password123", null)
        );
        User applicant2 = userService.registerUser(
                new User(0, "maria_gomez", "maria@email.com", "password456", null)
        );
        User recruiter1 = userService.registerUser(
                new User(0, "tech_recruiter", "recruiter@tech.com", "recruiterpass", null)
        );
        User recruiter2 = userService.registerUser(
                new User(0, "hr_sofia", "sofia@hr.com", "hrpass123", null)
        );

        // 2. Crear perfiles usando el Builder/Director desde ProfileService
        //    (aquí usamos createTechCandidate, asumiendo que define profesión y skills)
        User candidato1 = profileService.createTechCandidate(
                applicant1.getId(),
                applicant1.getUsername(),
                applicant1.getEmail(),
                "password123",                                  // Contraseña
                List.of("Java", "Spring Boot", "SQL")           // Skills
        );
        User candidato2 = profileService.createTechCandidate(
                applicant2.getId(),
                applicant2.getUsername(),
                applicant2.getEmail(),
                "password456",
                List.of("Selección", "Entrevistas", "Onboarding")
        );
        User candidato3 = profileService.createTechCandidate(
                recruiter1.getId(),
                recruiter1.getUsername(),
                recruiter1.getEmail(),
                "recruiterpass",
                List.of("IT Recruitment", "Headhunting")
        );
        User candidato4 = profileService.createTechCandidate(
                recruiter2.getId(),
                recruiter2.getUsername(),
                recruiter2.getEmail(),
                "hrpass123",
                List.of("Gestión de personal", "Capacitación")
        );

        // 3. Actualizar cada usuario con el perfil que viene embebido en el objeto User
        userService.updateUser(candidato1);
        userService.updateUser(candidato2);
        userService.updateUser(candidato3);
        userService.updateUser(candidato4);

        // 4. Crear empresas (igual que antes)…
        Company techCompany = companyService.createCompany(
                new Company(0,
                        "HR Consulting Partners",
                        "www.hrconsulting.com, Especialistas en capital humano",
                        "HUMAN_RESOURCES",
                        "456 Business St, New York"
                )
        );
        Company hrConsulting = companyService.createCompany(
                new Company(0,
                        "Global HR Solutions",
                        "www.globalhr.com, Soluciones integrales",
                        "HUMAN_RESOURCES",
                        "789 Corporate Ave, Lima"
                )
        );


        // 5. Insertar vacantes usando el Builder/Director
        List<String> javaReqs = List.of(
                "Conocimiento avanzado de estructuras de datos",
                "Carrera Universitaria de Sistemas o afines",
                "Conocimiento de Microservicios",
                "Experiencia con Scrum"
        );
        JobVacancy javaJob = jobService.createTechJob(
                "Desarrollador Java Senior",
                techCompany.getIdCompany(),
                javaReqs,
                60000.00  // ahora por encima de DEFAULT_BASE_SALARY
        );

        List<String> hrReqs = List.of(
                "Experiencia en IT Recruitment",
                "Conocimiento de tecnologías de desarrollo",
                "Inglés avanzado"
        );
        JobVacancy hrJob = jobService.createStandardJob(
                "Especialista en Reclutamiento IT",
                hrConsulting.getIdCompany(),
                hrReqs
        );

        // 6. Postulaciones
        postulationService.applyForJob(applicant1, javaJob.getId());
        postulationService.applyForJob(applicant2, hrJob.getId());

        // 7. Notificaciones
        notificationService.createNotification(
                new Notification(
                        0,
                        applicant1.getId(),
                        "Nueva oportunidad laboral",
                        "Tu perfil coincide con la vacante de Desarrollador Java Senior",
                        LocalDateTime.now(),
                        false
                )
        );
        notificationService.createNotification(
                new Notification(
                        0,
                        applicant2.getId(),
                        "Postulación recibida",
                        "HR Consulting ha recibido tu aplicación para Especialista en Reclutamiento IT",
                        LocalDateTime.now(),
                        false
                )
        );

        System.out.println("✅ Datos de prueba insertados exitosamente!");
    }

    public static void main(String[] args) {
        new Main();
    }
}
