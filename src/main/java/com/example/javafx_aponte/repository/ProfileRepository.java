package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository {
    Profile saveProfile(Profile profile);
    Optional<Profile> findByProfileId(int id);
    Optional<Profile> findByUserId(int userId);
    List<Profile> findAllProfiles();
    void deleteProfiles(int id);
    Profile updateProfile(Profile profile);
    List<Profile> findProfileByProfession(String profession);
}
