package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.JobVacancy;
import com.example.javafx_aponte.models.Postulation;
import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.util.PostulationStatus;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ConcretePostulationRepository implements PostulationRepository {
    private final Connection connection;

    public ConcretePostulationRepository(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public Postulation savePostulation(Postulation postulation) {
        String sql = postulation.getId() == 0 ?
                "INSERT INTO postulations (user_id, vacancy_id, status, postulation_date) VALUES (?, ?, ?, ?)" :
                "UPDATE postulations SET user_id = ?, vacancy_id = ?, status = ?, postulation_date = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, postulation.getUserId());  // Usamos getUserId()
            stmt.setInt(2, postulation.getJobVacancyId());  // Usamos getJobVacancyId()
            stmt.setString(3, postulation.getStatus().name());
            stmt.setDate(4, Date.valueOf(postulation.getPostulationDate()));

            if (postulation.getId() != 0) {
                stmt.setInt(5, postulation.getId());
            }

            stmt.executeUpdate();

            if (postulation.getId() == 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        postulation.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving postulation", e);
        }
        return postulation;
    }

    @Override
    public Optional<Postulation> findPostulationById(int id) {
        String sql = """
        SELECT p.*, u.name AS user_name, j.title AS job_title
          FROM postulations p
          JOIN users u ON p.user_id = u.id
          JOIN job_vacancies j ON p.vacancy_id = j.id
         WHERE p.id = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToPostulation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar postulación por ID: " + id, e);
        }
        return Optional.empty();
    }


    @Override
    public List<Postulation> findPostulationByUser(User user) {
        String sql = "SELECT p.*, j.title as job_title FROM postulations p " +
                "JOIN job_vacancies j ON p.vacancy_id = j.id " +
                "WHERE p.user_id = ?";
        return fetchPostulations(sql, user.getId());
    }

    @Override
    public List<Postulation> findPostulationByJobVacancy(int jobVacancyId) {
        String sql = "SELECT p.*, u.name as user_name FROM postulations p " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.vacancy_id = ?";
        return fetchPostulations(sql, jobVacancyId);
    }

    @Override
    public List<Postulation> findPostulationByStatus(PostulationStatus status) {
        String sql = "SELECT p.*, u.name as user_name, j.title as job_title FROM postulations p " +
                "JOIN users u ON p.user_id = u.id " +
                "JOIN job_vacancies j ON p.vacancy_id = j.id " +
                "WHERE p.status = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            return fetchPostulations(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding postulations by status", e);
        }
    }

    @Override
    public void updatePostulationStatus(int postulationId, PostulationStatus status) {
        String sql = "UPDATE postulations SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, postulationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating postulation status", e);
        }
    }

    @Override
    public boolean existsPostulationByUserAndJobVacancy(User user, int jobVacancyId) {
        String sql = "SELECT COUNT(*) FROM postulations WHERE user_id = ? AND vacancy_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, jobVacancyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking postulation existence", e);
        }
        return false;
    }

    @Override
    public void deletePostulation(int id) {
        String sql = "DELETE FROM postulations WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting postulation", e);
        }
    }

    private List<Postulation> fetchPostulations(String sql, int param) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, param);
            return fetchPostulations(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching postulations", e);
        }
    }

    private List<Postulation> fetchPostulations(PreparedStatement stmt) throws SQLException {
        List<Postulation> postulations = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                postulations.add(mapToPostulation(rs));
            }
        }
        return postulations;
    }

    private Postulation mapToPostulation(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getInt("user_id"),
                rs.getString("user_name"),
                null, null, null
        );

        JobVacancy job = new JobVacancy(
                rs.getInt("vacancy_id"),
                rs.getString("job_title"),
                null, null, null, List.of(), 0.0
        );

        return new Postulation(
                rs.getInt("id"),
                user,
                job,
                PostulationStatus.valueOf(rs.getString("status")),
                rs.getDate("postulation_date").toLocalDate()
        );
    }
}
