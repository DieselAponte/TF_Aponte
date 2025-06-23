package com.example.javafx_aponte.services;

import com.example.javafx_aponte.models.Profile;
import com.example.javafx_aponte.repository.ProfileRepository;
import com.example.javafx_aponte.repository.SkillRepository;
import com.example.javafx_aponte.repository.UserRepository;

import java.util.*;
import com.example.javafx_aponte.models.Profile;
import com.example.javafx_aponte.repository.ProfileRepository;
import com.example.javafx_aponte.repository.SkillRepository;
import com.example.javafx_aponte.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProfileService {
    private final ProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final SkillRepository skillRepo;

    public ProfileService(ProfileRepository profileRepo,
                          UserRepository userRepo,
                          SkillRepository skillRepo) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        this.skillRepo = skillRepo;
    }

    public Profile createProfile(Profile profile) {
        // Verificar si el usuario ya tiene un perfil
        if (profileRepo.findByUserId(profile.getUserId()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya tiene un perfil asignado");
        }

        try {
            // Validar usuario existente
            userRepo.findUserById(profile.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

            // Guardar perfil básico (sin skills primero)
            Profile newProfile = new Profile(
                    0, // ID será generado
                    profile.getUserId(),
                    profile.getProfession(),
                    new ArrayList<>(),
                    profile.getDescription()
            );

            Profile savedProfile = profileRepo.saveProfile(newProfile);

            // Asignar skills
            if (profile.getJobSkills() != null && !profile.getJobSkills().isEmpty()) {
                assignSkillsToProfile(savedProfile.getId(), profile.getJobSkills());
            }

            return getProfileById(savedProfile.getId())
                    .orElseThrow(() -> new RuntimeException("Perfil creado pero no se pudo recuperar"));
        } catch (Exception e) {
            throw new RuntimeException("Error al crear perfil: " + e.getMessage(), e);
        }
    }

    private void assignSkillsToProfile(int profileId, List<String> skills) {
        try {
            for (String skill : skills) {
                int skillId = skillRepo.getOrCreateSkill(skill);
                skillRepo.assignSkillToProfile(profileId, skillId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error asignando habilidades al perfil", e);
        }
    }

    public Optional<Profile> getProfileById(int id) {
        Optional<Profile> profileOpt = profileRepo.findByProfileId(id);
        profileOpt.ifPresent(profile ->
                profile.setJobSkills(skillRepo.findSkillsByProfile(id))
        );
        return profileOpt;
    }

    public Optional<Profile> getProfileByUser(int userId) {
        Optional<Profile> profileOpt = profileRepo.findByUserId(userId);
        profileOpt.ifPresent(profile ->
                profile.setJobSkills(skillRepo.findSkillsByProfile(profile.getId()))
        );
        return profileOpt;
    }

    public List<Profile> getAllProfiles() {
        List<Profile> profiles = profileRepo.findAllProfiles();
        profiles.forEach(profile ->
                profile.setJobSkills(skillRepo.findSkillsByProfile(profile.getId()))
        );
        return profiles;
    }

    public List<Profile> searchProfilesByProfession(String profession) {
        List<Profile> profiles = profileRepo.findProfileByProfession(profession);
        profiles.forEach(profile ->
                profile.setJobSkills(skillRepo.findSkillsByProfile(profile.getId()))
        );
        return profiles;
    }

    public List<Profile> searchProfilesBySkills(List<String> skills) {
        // Obtener IDs de skills primero
        List<Integer> skillIds = skills.stream()
                .map(skillRepo::getSkillIdByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        if (skillIds.isEmpty()) {
            return List.of();
        }

        // Obtener perfiles que tienen TODAS las skills solicitadas
        List<Integer> profileIds = skillRepo.findProfileIdsWithAllSkills(skillIds);

        return profileIds.stream()
                .map(this::getProfileById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public void deleteProfile(int id) {
        // Primero eliminar relaciones de skills
        skillRepo.deleteSkillsForProfile(id);
        // Luego eliminar el perfil
        profileRepo.deleteProfiles(id);
    }

    public Profile updateProfile(Profile profile) {
        // Verificar que el perfil exista
        Profile existingProfile = getProfileById(profile.getId())
                .orElseThrow(() -> new IllegalArgumentException("Perfil no existe"));

        try {
            // Actualizar datos básicos
            Profile updatedProfile = profileRepo.updateProfile(
                    new Profile(
                            profile.getId(),
                            existingProfile.getUserId(), // Mantener el user_id original
                            profile.getProfession(),
                            new ArrayList<>(), // Skills se manejan aparte
                            profile.getDescription()
                    )
            );

            // Actualizar skills (en transacción implícita)
            skillRepo.deleteSkillsForProfile(profile.getId());
            if (profile.getJobSkills() != null && !profile.getJobSkills().isEmpty()) {
                assignSkillsToProfile(profile.getId(), profile.getJobSkills());
            }

            return getProfileById(updatedProfile.getId())
                    .orElse(updatedProfile);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar perfil: " + e.getMessage(), e);
        }
    }
}