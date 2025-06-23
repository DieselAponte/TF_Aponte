package com.example.javafx_aponte.builder.builders;

import com.example.javafx_aponte.models.Profile;
import com.example.javafx_aponte.models.User;
import java.util.List;

public interface ProfileBuilder {
    ProfileBuilder reset();

    // Construcción de User
    ProfileBuilder withUserBasicInfo(int id, String username, String email);
    ProfileBuilder withUserPassword(String password);

    // Construcción de Profile
    ProfileBuilder withProfession(String profession);
    ProfileBuilder withDescription(String description);
    ProfileBuilder withSkills(List<String> skills);
    ProfileBuilder addSkill(String skill);

    // Resultado final
    User buildUserWithProfile();
    Profile buildProfileOnly();
}