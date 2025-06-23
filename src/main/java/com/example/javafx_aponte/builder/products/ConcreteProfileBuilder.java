package com.example.javafx_aponte.builder.products;

import com.example.javafx_aponte.builder.builders.ProfileBuilder;
import com.example.javafx_aponte.models.Profile;
import com.example.javafx_aponte.models.User;
import java.util.ArrayList;
import java.util.List;

public class ConcreteProfileBuilder implements ProfileBuilder {
    private User user;
    private Profile profile;
    private List<String> tempSkills = new ArrayList<>();

    @Override
    public ProfileBuilder reset() {
        this.user = null;
        this.profile = null;
        this.tempSkills.clear();
        return this;
    }

    @Override
    public ProfileBuilder withUserBasicInfo(int id, String username, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.user = new User(id, username, email, null, null);
        return this;
    }

    @Override
    public ProfileBuilder withUserPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (this.user == null) {
            throw new IllegalStateException("Set basic info first");
        }
        this.user.setPassword(password);
        return this;
    }

    @Override
    public ProfileBuilder withProfession(String profession) {
        if (profession == null || profession.trim().isEmpty()) {
            throw new IllegalArgumentException("Profession cannot be empty");
        }
        if (this.profile == null) {
            this.profile = new Profile(0, 0, profession, new ArrayList<>(), "");
        } else {
            this.profile.setProfession(profession);
        }
        return this;
    }

    @Override
    public ProfileBuilder withDescription(String description) {
        if (description == null) description = ""; // Descripción opcional
        if (this.profile == null) {
            throw new IllegalStateException("Set profession first");
        }
        this.profile.setDescription(description);
        return this;
    }

    @Override
    public ProfileBuilder withSkills(List<String> skills) {
        if (skills == null) {
            throw new IllegalArgumentException("Skills list cannot be null");
        }
        this.tempSkills.addAll(skills);
        return this;
    }

    @Override
    public ProfileBuilder addSkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill cannot be empty");
        }
        this.tempSkills.add(skill);
        return this;
    }

    @Override
    public User buildUserWithProfile() {
        if (this.user == null) {
            throw new IllegalStateException("User basic info not set");
        }
        if (this.profile == null) {
            throw new IllegalStateException("Profile not initialized");
        }

        // Asignar ID de usuario al perfil
        this.profile.setUserId(this.user.getId());

        // Agregar habilidades validadas
        this.tempSkills.forEach(skill -> {
            if (!this.profile.getJobSkills().contains(skill)) {
                this.profile.getJobSkills().add(skill);
            }
        });

        this.user.setProfile(this.profile);
        return this.user;
    }

    @Override
    public Profile buildProfileOnly() {
        if (this.profile == null) {
            throw new IllegalStateException("Profile not initialized");
        }
        if (this.tempSkills.isEmpty() && this.profile.getJobSkills().isEmpty()) {
            throw new IllegalStateException("At least one skill is required");
        }

        this.tempSkills.forEach(skill -> {
            if (!this.profile.getJobSkills().contains(skill)) {
                this.profile.getJobSkills().add(skill);
            }
        });

        return this.profile;
    }
}