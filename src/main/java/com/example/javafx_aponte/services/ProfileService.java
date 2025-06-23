package com.example.javafx_aponte.services;

import com.example.javafx_aponte.builder.builders.ProfileBuilder;
import com.example.javafx_aponte.builder.products.ConcreteProfileBuilder;
import com.example.javafx_aponte.builder.directors.UserProfileDirector;
import com.example.javafx_aponte.models.Profile;
import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.repository.ProfileRepository;
import com.example.javafx_aponte.repository.SkillRepository;
import com.example.javafx_aponte.repository.UserRepository;
import com.example.javafx_aponte.util.PostulationStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileService {
    private final ProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final SkillRepository skillRepo;
    private final UserProfileDirector director;

    public ProfileService(ProfileRepository profileRepo,
                          UserRepository userRepo,
                          SkillRepository skillRepo) {
        this.profileRepo   = profileRepo;
        this.userRepo      = userRepo;
        this.skillRepo     = skillRepo;
        this.director      = new UserProfileDirector();
    }

    /**
     * Crea un nuevo usuario + perfil “tech” validado por el Builder/Director.
     * @param id             ID del usuario
     * @param username       Nombre de usuario
     * @param email          Email
     * @param rawPassword    Contraseña
     * @param initialSkills  Lista de skills iniciales
     * @return Usuario ya persistido con su perfil
     */
    public User createTechCandidate(int id,
                                    String username,
                                    String email,
                                    String rawPassword,
                                    List<String> initialSkills) {
        ProfileBuilder builder = new ConcreteProfileBuilder();

        // 1) Montar User + Profile
        User candidate = director.createTechCandidate(
                builder, id, username, email, initialSkills);
        builder.withUserPassword(rawPassword);

        // 2) Primero, guardar el Profile
        Profile profileToSave = candidate.getProfile();
        profileToSave.setUserId(candidate.getId());
        Profile savedProfile = profileRepo.saveProfile(profileToSave);

        // 3) Asignar el nuevo profile_id al User antes de salvarlo
        candidate.setProfile(savedProfile);

        // 4) Guardar User (ahora profile_id existe en BD)
        User savedUser = userRepo.saveUser(candidate);

        // 5) Asignar skills en la tabla intermedia
        assignSkillsToProfile(savedProfile.getId(), initialSkills);

        return savedUser;
    }


    private void assignSkillsToProfile(int profileId, List<String> skills) {
        skills.forEach(skillName -> {
            int skillId = skillRepo.getOrCreateSkill(skillName);
            skillRepo.assignSkillToProfile(profileId, skillId);
        });
    }

    /**
     * Reconstruye y valida un Profile existente antes de actualizar.
     */
    public Profile updateProfile(Profile rawProfile,
                                 String newDescription,
                                 List<String> newSkills) {
        // 1) Asegurarnos de que existe
        Profile existing = profileRepo.findByProfileId(rawProfile.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil no existe"));

        // 2) Usar el Builder en modo “solo Profile”
        ProfileBuilder builder = new ConcreteProfileBuilder();
        Profile rebuilt = builder.reset()
                .withProfession(rawProfile.getProfession())
                .withDescription(newDescription)
                .withSkills(newSkills)
                .buildProfileOnly();

        // 3) Persistir cambios básicos
        Profile updated = profileRepo.updateProfile(rebuilt);

        // 4) Reasignar skills (eliminar + volver a asignar)
        skillRepo.deleteSkillsForProfile(updated.getId());
        assignSkillsToProfile(updated.getId(), newSkills);

        // 5) Cargar skills y devolver
        return getProfileById(updated.getId()).orElse(updated);
    }

    /* ---------------------- Métodos de consulta “puros” ---------------------- */

    public Optional<Profile> getProfileById(int id) {
        Optional<Profile> opt = profileRepo.findByProfileId(id);
        opt.ifPresent(p -> p.setJobSkills(skillRepo.findSkillsByProfile(id)));
        return opt;
    }

    public Optional<Profile> getProfileByUser(int userId) {
        Optional<Profile> opt = profileRepo.findByUserId(userId);
        opt.ifPresent(p -> p.setJobSkills(skillRepo.findSkillsByProfile(p.getId())));
        return opt;
    }

    public List<Profile> getAllProfiles() {
        List<Profile> list = profileRepo.findAllProfiles();
        list.forEach(p -> p.setJobSkills(skillRepo.findSkillsByProfile(p.getId())));
        return list;
    }

    public List<Profile> searchProfilesByProfession(String profession) {
        List<Profile> list = profileRepo.findProfileByProfession(profession);
        list.forEach(p -> p.setJobSkills(skillRepo.findSkillsByProfile(p.getId())));
        return list;
    }

    public List<Profile> searchProfilesBySkills(List<String> skills) {
        // 1) Obtener IDs de skills
        List<Integer> skillIds = skills.stream()
                .map(skillRepo::getSkillIdByName)
                .flatMap(Optional::stream)
                .toList();

        if (skillIds.isEmpty()) return List.of();

        // 2) Buscar perfiles que tienen todas esas skills
        List<Integer> profileIds = skillRepo.findProfileIdsWithAllSkills(skillIds);

        // 3) Cargar cada Profile completo
        return profileIds.stream()
                .map(this::getProfileById)
                .flatMap(Optional::stream)
                .toList();
    }

    public void deleteProfile(int id) {
        skillRepo.deleteSkillsForProfile(id);
        profileRepo.deleteProfiles(id);
    }
}
