package com.example.javafx_aponte.builder.directors;

import com.example.javafx_aponte.builder.builders.ProfileBuilder;
import com.example.javafx_aponte.models.User;

import java.util.List;

public class UserProfileDirector {
    public User createStandardCandidate(ProfileBuilder builder, int id, String username, String email, String profession) {
        return builder.reset()
                .withUserBasicInfo(id, username, email)
                .withProfession(profession)
                .withDescription("")
                .addSkill("Communication")
                .buildUserWithProfile();
    }

    public User createTechCandidate(ProfileBuilder builder, int id, String username, String email, List<String> techSkills) {
        return builder.reset()
                .withUserBasicInfo(id, username, email)
                .withProfession("Software Developer")
                .withSkills(techSkills)
                .withDescription("Experienced in modern technologies")
                .buildUserWithProfile();
    }
}