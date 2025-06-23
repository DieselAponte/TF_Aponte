package com.example.javafx_aponte.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConcreteSkillRepository implements SkillRepository {
    private final Connection connection;

    public ConcreteSkillRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int getOrCreateSkill(String skillName) {
        String selectSql = "SELECT skill_id FROM skills WHERE skill_name = ?";
        String insertSql = "INSERT INTO skills (skill_name) VALUES (?)";

        try {
            // Intentar obtener primero
            try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
                stmt.setString(1, skillName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("skill_id");
                }
            }

            // Si no existe, crear
            try (PreparedStatement stmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, skillName);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error gestionando skill: " + skillName, e);
        }
        throw new RuntimeException("No se pudo obtener o crear el skill: " + skillName);
    }

    @Override
    public Optional<Integer> getSkillIdByName(String skillName) {
        String sql = "SELECT skill_id FROM skills WHERE skill_name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, skillName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getInt("skill_id"));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ID de habilidad: " + skillName, e);
        }
    }

    @Override
    public List<String> findSkillsByProfile(int profileId) {
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
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener habilidades del perfil ID: " + profileId, e);
        }

        return skills;
    }

    @Override
    public void assignSkillToProfile(int profileId, int skillId) {
        String sql = "INSERT INTO profile_skills (profile_id, skill_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            stmt.setInt(2, skillId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar habilidad al perfil", e);
        }
    }

    @Override
    public void deleteSkillsForProfile(int profileId) {
        String sql = "DELETE FROM profile_skills WHERE profile_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar habilidades del perfil ID: " + profileId, e);
        }
    }

    @Override
    public List<Integer> findProfileIdsWithAllSkills(List<Integer> skillIds) {
        List<Integer> profileIds = new ArrayList<>();
        if (skillIds.isEmpty()) {
            return profileIds;
        }

        // Construir la parte IN de la consulta dinámicamente
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < skillIds.size(); i++) {
            inClause.append("?");
            if (i < skillIds.size() - 1) {
                inClause.append(",");
            }
        }

        String sql = "SELECT ps.profile_id FROM profile_skills ps " +
                "WHERE ps.skill_id IN (" + inClause + ") " +
                "GROUP BY ps.profile_id " +
                "HAVING COUNT(DISTINCT ps.skill_id) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Establecer los parámetros para los skill_ids
            for (int i = 0; i < skillIds.size(); i++) {
                stmt.setInt(i + 1, skillIds.get(i));
            }
            // Establecer el parámetro para el HAVING COUNT (número de skills)
            stmt.setInt(skillIds.size() + 1, skillIds.size());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                profileIds.add(rs.getInt("profile_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar perfiles con habilidades específicas", e);
        }

        return profileIds;
    }
}