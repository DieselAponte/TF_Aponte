package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.Profile;

import java.sql.*;
import java.util.*;

public class ConcreteProfileRepository implements ProfileRepository {
    private final Connection connection;

    public ConcreteProfileRepository(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public Profile saveProfile(Profile profile) {
        String sql = profile.getId() == 0 ?
                "INSERT INTO profiles (user_id, profession, description) VALUES (?, ?, ?)" :
                "UPDATE profiles SET profession = ?, description = ? WHERE id = ?"; // Eliminé user_id del UPDATE

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (profile.getId() == 0) {
                stmt.setInt(1, profile.getUserId());
                stmt.setString(2, profile.getProfession());
                stmt.setString(3, profile.getDescription());
            } else {
                stmt.setString(1, profile.getProfession());
                stmt.setString(2, profile.getDescription());
                stmt.setInt(3, profile.getId());
            }

            stmt.executeUpdate();

            if (profile.getId() == 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        profile.setId(rs.getInt(1));
                    }
                }
            }
            saveProfileSkills(profile); // Mover fuera del if para ambos casos
        } catch (SQLException e) {
            throw new RuntimeException("Error saving profile", e);
        }
        return profile;
    }

    @Override
    public Optional<Profile> findByProfileId(int id) {
        String sql = "SELECT * FROM profiles WHERE profile_id = ?"; // Cambiado "id" por "profile_id"

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Profile profile = mapToProfile(rs);
                profile.setJobSkills(getProfileSkills(id));
                return Optional.of(profile);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding profile by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Profile> findByUserId(int userId) {
        String sql = "SELECT * FROM profiles WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Profile profile = mapToProfile(rs);
                profile.setJobSkills(getProfileSkills(profile.getId()));
                return Optional.of(profile);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding profile by user ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Profile> findAllProfiles() {
        Map<Integer, Profile> profileMap = new HashMap<>();
        String sql = "SELECT p.*, s.skill_name FROM profiles p " +
                "LEFT JOIN profile_skills ps ON p.id = ps.profile_id " +
                "LEFT JOIN skills s ON ps.skill_id = s.skill_id " +
                "ORDER BY p.id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int profileId = rs.getInt("id");
                Profile profile = profileMap.computeIfAbsent(profileId, id -> {
                    try {
                        return mapToProfile(rs);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                String skill = rs.getString("skill_name");
                if (skill != null) {
                    profile.getJobSkills().add(skill);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos los perfiles", e);
        }

        return new ArrayList<>(profileMap.values());
    }

    @Override
    public void deleteProfiles(int id) {
        String deleteSkillsSql = "DELETE FROM profile_skills WHERE profile_id = ?";
        String deleteProfileSql = "DELETE FROM profiles WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(deleteSkillsSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = connection.prepareStatement(deleteProfileSql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Error during rollback", ex);
            }
            throw new RuntimeException("Error deleting profile", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error resetting auto-commit", e);
            }
        }
    }

    @Override
    public Profile updateProfile(Profile profile) {
        return saveProfile(profile);
    }

    @Override
    public List<Profile> findProfileByProfession(String profession) {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profiles WHERE profession LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + profession + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Profile profile = mapToProfile(rs);
                profile.setJobSkills(getProfileSkills(profile.getId()));
                profiles.add(profile);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding profiles by profession", e);
        }
        return profiles;
    }

//    @Override
//    public List<Profile> findProfileBySkills(List<String> skills) {
//        List<Profile> profiles = new ArrayList<>();
//        if (skills.isEmpty()) return profiles;
//
//        String placeholders = String.join(",", Collections.nCopies(skills.size(), "?"));
//        String sql = "SELECT p.* FROM profiles p " +
//                "JOIN profile_skills ps ON p.id = ps.profile_id " +
//                "JOIN skills s ON ps.skill_id = s.skill_id " +
//                "WHERE s.skill_name IN (" + placeholders + ") " +
//                "GROUP BY p.id HAVING COUNT(DISTINCT s.skill_name) = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            // Establecer parámetros para los skills
//            int i = 1;
//            for (String skill : skills) {
//                stmt.setString(i++, skill);
//            }
//            // Establecer el parámetro para el HAVING COUNT
//            stmt.setInt(i, skills.size());
//
//            ResultSet rs = stmt.executeQuery();
//            while (rs.next()) {
//                Profile profile = mapToProfile(rs);
//                profile.setJobSkills(getProfileSkills(profile.getId()));
//                profiles.add(profile);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error al buscar perfiles por habilidades", e);
//        }
//
//        return profiles;
//    }


    // Métodos auxiliares
    private Profile mapToProfile(ResultSet rs) throws SQLException {
        return new Profile(
                rs.getInt("profile_id"), // Cambiado de "id" a "profile_id"
                rs.getInt("user_id"),
                rs.getString("profession"),
                new ArrayList<>(),
                rs.getString("description")
        );
    }

    private List<String> getProfileSkills(int profileId) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT s.skill_name FROM skills s " +
                "JOIN profile_skills ps ON s.skill_id = ps.skill_id " +
                "WHERE ps.profile_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                skills.add(rs.getString("skill_name"));
            }
        }
        return skills;
    }



    private void saveProfileSkills(Profile profile) throws SQLException {
        // Primero eliminar habilidades existentes
        deleteProfileSkills(profile.getId());

        // Insertar las nuevas habilidades
        String sql = "INSERT INTO profile_skills (profile_id, skill_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String skillName : profile.getJobSkills()) {
                int skillId = getOrCreateSkillId(skillName);
                stmt.setInt(1, profile.getId());
                stmt.setInt(2, skillId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private int getOrCreateSkillId(String skillName) throws SQLException {
        // 1. Buscar skill existente
        String selectSql = "SELECT skill_id FROM skills WHERE skill_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            stmt.setString(1, skillName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("skill_id");
            }
        }

        // 2. Crear nuevo skill si no existe
        String insertSql = "INSERT INTO skills (skill_name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, skillName);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("No se pudo crear o encontrar el skill: " + skillName);
    }

    private void deleteProfileSkills(int profileId) throws SQLException {
        String sql = "DELETE FROM profile_skills WHERE profile_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            stmt.executeUpdate();
        }
    }

    private void updateProfileSkills(Profile profile) throws SQLException {
        String deleteSql = "DELETE FROM profile_skills WHERE profile_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, profile.getId());
            stmt.executeUpdate();
        }
        saveProfileSkills(profile);
    }
}