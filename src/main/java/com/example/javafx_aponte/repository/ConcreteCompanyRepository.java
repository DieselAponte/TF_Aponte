package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.repository.CompanyRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConcreteCompanyRepository implements CompanyRepository {
    private final Connection connection;

    public ConcreteCompanyRepository(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public Company saveCompany(Company company) {
        System.out.println("Guardando compañía: " + company.getNameCompany() + " - ID: " + company.getIdCompany());
        String sql = company.getIdCompany() == 0 ?
                "INSERT INTO companies (name, description, job_sector, address) VALUES (?, ?, ?, ?)" :
                "UPDATE companies SET name = ?, description = ?, job_sector = ?, address = ? WHERE company_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, company.getNameCompany());
            stmt.setString(2, company.getDescription());
            stmt.setString(3, company.getJobSector());
            stmt.setString(4, company.getCompanyAddress());

            if (company.getIdCompany() != 0) {
                stmt.setInt(5, company.getIdCompany());
            }

            stmt.executeUpdate();

            if (company.getIdCompany() == 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        company.setIdCompany(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving company", e);
        }
        return company;
    }

    @Override
    public Optional<Company> findCompanyById(int id) {
        System.out.println("Buscando compañía con ID: " + id);
        final String sql = "SELECT * FROM companies WHERE company_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Company company = new Company(
                            rs.getInt("company_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("job_sector"),
                            rs.getString("address")
                    );
                    return Optional.of(company);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding company by ID: " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Company> findAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                companies.add(mapToCompany(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching companies", e);
        }
        return companies;
    }

    @Override
    public List<Company> findCompanyBySector(String sector) {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies WHERE job_sector LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + sector + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                companies.add(mapToCompany(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding companies by sector", e);
        }
        return companies;
    }

    @Override
    public void deleteCompanyById(int id) {
        String sql = "DELETE FROM companies WHERE company_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting company", e);
        }
    }

    @Override
    public Company updateCompanyInformation(Company company) {
        return saveCompany(company);
    }

    @Override
    public List<Company> findCompanyByNameContaining(String name) {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies WHERE name LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                companies.add(mapToCompany(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding companies by name", e);
        }
        return companies;
    }

    private Company mapToCompany(ResultSet rs) throws SQLException {
        return new Company(
                rs.getInt("company_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("job_sector"),
                rs.getString("address")
        );
    }
}