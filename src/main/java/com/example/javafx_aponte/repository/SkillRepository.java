package com.example.javafx_aponte.repository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository {
    int getOrCreateSkill(String skillName);
    Optional<Integer> getSkillIdByName(String skillName);
    List<String> findSkillsByProfile(int profileId);
    void assignSkillToProfile(int profileId, int skillId);
    void deleteSkillsForProfile(int profileId);
    List<Integer> findProfileIdsWithAllSkills(List<Integer> skillIds);
}