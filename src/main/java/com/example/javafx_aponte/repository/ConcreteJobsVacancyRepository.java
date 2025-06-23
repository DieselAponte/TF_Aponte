package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.models.JobVacancies;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ConcreteJobsVacancyRepository implements JobVacancyRepository {
    private final Connection connection;

    public  ConcreteJobsVacancyRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public JobVacancies saveJobVacancy(JobVacancies vacancy) {
        String sql = vacancy.getId() == 0 ?
                "INSERT INTO job_vacancies (title, description, company_id, publication_date, salary) VALUES (?, ?, ?, ?, ?)" :
                "UPDATE job_vacancies SET title = ?, description = ?, company_id = ?, publication_date = ?, salary = ? WHERE vacancy_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vacancy.getTitle());
            stmt.setString(2, vacancy.getDescription());
            stmt.setInt(3, vacancy.getCompany().getIdCompany());
            stmt.setDate(4, Date.valueOf(vacancy.getPublicationDate()));
            stmt.setDouble(5, vacancy.getSalary());

            if (vacancy.getId() != 0) {
                stmt.setInt(6, vacancy.getId());
            }

            stmt.executeUpdate();

            if (vacancy.getId() == 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        vacancy.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving job vacancy", e);
        }
        return vacancy;
    }

    @Override
    public Optional<JobVacancies> findJobVacancyById(Integer id) {
        String sql = "SELECT j.*, c.name, c.address, c.description, c.job_sector FROM job_vacancies j " +
                "JOIN companies c ON j.company_id = c.company_id WHERE j.vacancy_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapToJobVacancy(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding job vacancy by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<JobVacancies> findAllJobVacancies() {
        String sql = "SELECT j.*, c.name, c.address, c.description, c.job_sector FROM job_vacancies j " +
                "JOIN companies c ON j.company_id = c.company_id";
        return fetchJobVacancies(sql);
    }

    @Override
    public List<JobVacancies> findJobVacanciesByCompanyId(int companyId) {
        String sql = "SELECT j.*, c.name, c.address, c.description, c.job_sector FROM job_vacancies j " +
                "JOIN companies c ON j.company_id = c.company_id WHERE j.company_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            return fetchJobVacancies(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding vacancies by company ID", e);
        }
    }

    @Override
    public List<JobVacancies> findJobVacanciesByCompanyName(String companyName) {
        String sql = "SELECT j.*, c.name, c.address, c.description, c.job_sector FROM job_vacancies j " +
                "JOIN companies c ON j.company_id = c.company_id WHERE c.name LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + companyName + "%");
            return fetchJobVacancies(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding vacancies by company name", e);
        }
    }

    @Override
    public List<JobVacancies> findJobVacanciesByKeyword(String keyword) {
        String sql = "SELECT j.*, c.name, c.address, c.description, c.job_sector FROM job_vacancies j " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "WHERE j.title LIKE ? OR j.description LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchTerm = "%" + keyword + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            return fetchJobVacancies(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding vacancies by keyword", e);
        }
    }

    @Override
    public List<JobVacancies> findJobVacanciesBySalaryRange(double min, double max) {
        String sql = "SELECT j.*, c.name, c.address, c.description, c.job_sector FROM job_vacancies j " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "WHERE j.salary BETWEEN ? AND ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, min);
            stmt.setDouble(2, max);
            return fetchJobVacancies(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding vacancies by salary range", e);
        }
    }

    @Override
    public void deleteJobVacancy(int id) {
        String sql = "DELETE FROM job_vacancies WHERE vacancy_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting job vacancy", e);
        }
    }

    @Override
    public JobVacancies updateJobVacancy(JobVacancies vacancy) {
        return saveJobVacancy(vacancy);
    }

    @Override
    public List<JobVacancies> findJobVacanciesBySkills(List<String> skills) {
        String sql = "SELECT j.*, c.name, c.address, c.description, c.job_sector FROM job_vacancies j " +
                "JOIN companies c ON j.company_id = c.company_id " +
                "JOIN job_requirements r ON j.vacancy_id = r.vacancy_id " +
                "WHERE r.requirement IN (" + String.join(",", Collections.nCopies(skills.size(), "?")) + ") " +
                "GROUP BY j.vacancy_id HAVING COUNT(DISTINCT r.requirement) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int i = 1;
            for (String skill : skills) {
                stmt.setString(i++, skill);
            }
            stmt.setInt(i, skills.size());
            return fetchJobVacancies(stmt);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding vacancies by skills", e);
        }
    }

    private List<JobVacancies> fetchJobVacancies(String sql) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return fetchJobVacancies(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching job vacancies", e);
        }
    }

    private List<JobVacancies> fetchJobVacancies(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            return fetchJobVacancies(rs);
        }
    }

    private List<JobVacancies> fetchJobVacancies(ResultSet rs) throws SQLException {
        List<JobVacancies> vacancies = new ArrayList<>();
        while (rs.next()) {
            vacancies.add(mapToJobVacancy(rs));
        }
        return vacancies;
    }

    private JobVacancies mapToJobVacancy(ResultSet rs) throws SQLException {
        Company company = new Company(
                rs.getInt("company_id"),
                rs.getString("name"),      // Cambiado de name_company
                rs.getString("description"),
                rs.getString("job_sector"),
                rs.getString("address")    // Cambiado de company_address
        );

        return new JobVacancies(
                rs.getInt("vacancy_id"),   // Cambiado de id
                rs.getString("title"),
                rs.getString("description"),
                company,
                rs.getDate("publication_date").toLocalDate(),
                getJobRequirements(rs.getInt("vacancy_id")), // Cambiado de id
                rs.getDouble("salary")
        );
    }

    private List<String> getJobRequirements(int jobId) throws SQLException {
        List<String> requirements = new ArrayList<>();
        String sql = "SELECT requirement FROM job_requirements WHERE vacancy_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                requirements.add(rs.getString("requirement"));
            }
        }
        return requirements;
    }
}