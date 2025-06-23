package com.example.javafx_aponte;

import com.example.javafx_aponte.models.*;
import com.example.javafx_aponte.repository.*;
import com.example.javafx_aponte.services.*;
import com.example.javafx_aponte.util.PostulationStatus;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private final UserService userService;
    private final JobService jobService;
    private final NotificationService notificationService;
    private final ProfileService profileService;
    private final PostulationService postulationService;
    private final CompanyService companyService;


    public Main() {
        // Inicialización con valores temporales
        UserService userServiceTemp = null;
        JobService jobServiceTemp = null;
        NotificationService notificationServiceTemp = null;
        ProfileService profileServiceTemp = null;
        PostulationService postulationServiceTemp = null;
        CompanyService companyServiceTemp = null;


        try (Connection connection = DataBaseConnection.getConnection()) {

            // 1. Inicializar repositorios
            UserRepository userRepo = new ConcreteUserRepository(connection);
            CompanyRepository companyRepo = new ConcreteCompanyRepository(connection);
            JobVacancyRepository jobRepo = new ConcreteJobsVacancyRepository(connection);
            PostulationRepository postulationRepo = new ConcretePostulationRepository(connection);
            NotificationRepository notificationRepo = new ConcreteNotificationRepository(connection);
            ProfileRepository profileRepo = new ConcreteProfileRepository(connection);
            SkillRepository skillRepo = new ConcreteSkillRepository(connection);

            // 2. Inicializar servicios
            userServiceTemp = new UserService(userRepo);
            companyServiceTemp = new CompanyService(companyRepo);
            jobServiceTemp = new JobService(jobRepo, companyRepo);
            notificationServiceTemp = new NotificationService(notificationRepo);
            profileServiceTemp = new ProfileService(profileRepo, userRepo, skillRepo);
            postulationServiceTemp = new PostulationService(postulationRepo, userRepo, jobRepo);


            // 3. Insertar datos de prueba
            insertTestData(
                    userServiceTemp,
                    companyServiceTemp,
                    jobServiceTemp,
                    profileServiceTemp,
                    postulationServiceTemp,
                    notificationServiceTemp
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos: " + e.getMessage());
            System.exit(1);
        }

        // Asignar los servicios a los campos finales
        this.userService = userServiceTemp;
        this.jobService = jobServiceTemp;
        this.notificationService = notificationServiceTemp;
        this.profileService = profileServiceTemp;
        this.postulationService = postulationServiceTemp;
        this.companyService = companyServiceTemp;

    }

    private void insertTestData(
            UserService userService,
            CompanyService companyService,
            JobService jobService,
            ProfileService profileService,
            PostulationService postulationService,
            NotificationService notificationService) {

        // 1. Insertar usuarios primero (sin perfiles inicialmente)
        User applicant1 = new User(0, "juan_perez", "juan@email.com", "password123", null);
        applicant1 = userService.registerUser(applicant1);

        User applicant2 = new User(0, "maria_gomez", "maria@email.com", "password456", null);
        applicant2 = userService.registerUser(applicant2);

        User recruiter1 = new User(0, "tech_recruiter", "recruiter@tech.com", "recruiterpass", null);
        recruiter1 = userService.registerUser(recruiter1);

        User recruiter2 = new User(0, "hr_sofia", "sofia@hr.com", "hrpass123", null);
        recruiter2 = userService.registerUser(recruiter2);


        // 2. Crear perfiles usando el constructor correcto
        Profile juanPerezProfile = new Profile(
                0,
                applicant1.getId(),
                "Ingeniero de Software",
                Arrays.asList("Java", "Spring Boot", "SQL"),
                "5 años de experiencia desarrollando aplicaciones empresariales con Java"
        );
        juanPerezProfile = profileService.createProfile(juanPerezProfile);

        Profile mariaGomezProfile = new Profile(
                0,
                applicant2.getId(),
                "Reclutadora IT",
                Arrays.asList("Selección", "Entrevistas", "Onboarding"),
                "3 años reclutando talento tecnológico para empresas de TI"
        );
        mariaGomezProfile = profileService.createProfile(mariaGomezProfile);

        Profile carlosTechProfile = new Profile(
                0,
                recruiter1.getId(),
                "Tech Recruiter",
                Arrays.asList("IT Recruitment", "Headhunting"),
                "4 años reclutando desarrolladores para startups tecnológicas"
        );
        carlosTechProfile = profileService.createProfile(carlosTechProfile);

        Profile sofiaHrProfile = new Profile(
                0,
                recruiter2.getId(),
                "HR Manager",
                Arrays.asList("Gestión de personal", "Capacitación"),
                "6 años gestionando equipos de recursos humanos en multinacionales"
        );
        sofiaHrProfile = profileService.createProfile(sofiaHrProfile);


        Company techCompany = new Company(
                0, "HR Consulting Partners",
                "www.hrconsulting.com, Especialistas en capital humano",
                "HUMAN_RESOURCES",
                "456 Business St, New York"
        );
        // 3. Actualizar usuarios con sus perfiles
        applicant1.setProfile(juanPerezProfile);
        applicant2.setProfile(mariaGomezProfile);
        recruiter1.setProfile(carlosTechProfile);
        recruiter2.setProfile(sofiaHrProfile);

        userService.updateUser(applicant1);
        userService.updateUser(applicant2);
        userService.updateUser(recruiter1);
        userService.updateUser(recruiter2);
        techCompany = companyService.createCompany(techCompany);

        Company hrConsulting = new Company(
                0, "HR Consulting Partners",
                "www.hrconsulting.com, Especialistas en capital humano",
                "HUMAN_RESOURCES",
                "456 Business St, New York"
        );
        hrConsulting = companyService.createCompany(hrConsulting);

        List<String> jobRequirements = new ArrayList<>();
        jobRequirements.add("Conocimiento avanzado de estructuras de datos");
        jobRequirements.add("Carrera Universitaria de Sistemas o afines ");
        jobRequirements.add("Conocimiento de Microservicios");
        jobRequirements.add("Experienca con Scrum");
        // 4. Insertar vacantes
        JobVacancy javaJob = new JobVacancy(
                0, "Desarrollador Java Senior",
                "Desarrollo de aplicaciones empresariales con Spring Boot",
                techCompany,
                LocalDate.now(),
                jobRequirements,
                6000.00
        );
        javaJob = jobService.createJobVacancy(javaJob);

        // 4. Insertar vacantes (usando el constructor correcto)

        JobVacancy hrJob = new JobVacancy(
                0,
                "Especialista en Reclutamiento IT",
                "Gestión de talento tecnológico para empresas Fortune 500",
                hrConsulting,
                LocalDate.now(),
                Arrays.asList(
                        "Experiencia en IT Recruitment",
                        "Conocimiento de tecnologías de desarrollo",
                        "Inglés avanzado"
                ),
                4000.00
        );
        hrJob = jobService.createJobVacancy(hrJob);

// 5. Insertar postulaciones (usando el constructor correcto)
        Postulation postulation1 = new Postulation(
                0,
                applicant1,
                javaJob,
                PostulationStatus.EN_GESTION,
                LocalDate.now()
        );
        postulationService.applyForJob(applicant1,hrJob.getId());

        Postulation postulation2 = new Postulation(
                0,
                applicant2,
                hrJob,
                PostulationStatus.EN_GESTION,
                LocalDate.now()
        );
        postulationService.applyForJob(applicant2,hrJob.getId());

// 6. Insertar notificaciones (usando el constructor correcto)
        Notification notification1 = new Notification(
                0,
                applicant1.getId(),
                "Nueva oportunidad laboral",
                "Tu perfil coincide con la vacante de Desarrollador Java Senior",
                LocalDateTime.now(),
                false
        );
        notificationService.createNotification(notification1);

        Notification notification2 = new Notification(
                0,
                applicant2.getId(),
                "Postulación recibida",
                "HR Consulting ha recibido tu aplicación para Especialista en Reclutamiento IT",
                LocalDateTime.now(),
                false
        );
        notificationService.createNotification(notification2);

        System.out.println("✅ Datos de prueba insertados exitosamente!");
    }

    public static void main(String[] args) {
        new Main(); // Esto ejecutará el constructor e insertará los datos
    }
}

//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        // Código de inicio de JavaFX (opcional)
//        FXMLLoader loader = new FXMLLoader(
//                getClass().getResource("/com/example/javafx_aponte/views/MainPageView.fxml"));
//        Parent root = loader.load();
//        Scene scene = new Scene(root, 1200, 700);
//        primaryStage.setTitle("Aplicación JavaFX");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
