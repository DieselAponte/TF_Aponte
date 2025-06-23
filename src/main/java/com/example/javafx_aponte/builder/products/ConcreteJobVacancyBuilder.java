package com.example.javafx_aponte.builder.products;

import com.example.javafx_aponte.builder.builders.JobVacancyBuilder;
import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.models.JobVacancy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.models.JobVacancy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConcreteJobVacancyBuilder implements JobVacancyBuilder {
    private JobVacancy jobVacancy;
    private List<String> tempRequirements = new ArrayList<>();

    @Override
    public JobVacancyBuilder reset() {
        this.jobVacancy = new JobVacancy(0, "", "", null, LocalDate.now(), new ArrayList<>(), 0.0);
        this.tempRequirements.clear();
        return this;
    }

    @Override
    public JobVacancyBuilder withId(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive");
        jobVacancy.setId(id);
        return this;
    }

    @Override
    public JobVacancyBuilder withTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        jobVacancy.setTitle(title);
        return this;
    }

    @Override
    public JobVacancyBuilder withDescription(String description) {
        if (description == null || description.length() < 30) {
            throw new IllegalArgumentException("Description must be at least 30 characters");
        }
        jobVacancy.setDescription(description);
        return this;
    }

    @Override
    public JobVacancyBuilder withCompany(Company company) {
        if (company == null) throw new IllegalArgumentException("Company cannot be null");
        jobVacancy.setCompany(company);
        return this;
    }

    @Override
    public JobVacancyBuilder withPublicationDate(LocalDate date) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid publication date");
        }
        jobVacancy.setPublicationDate(date);
        return this;
    }

    @Override
    public JobVacancyBuilder withRequirements(List<String> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            throw new IllegalArgumentException("At least one requirement is needed");
        }
        this.tempRequirements.addAll(requirements);
        return this;
    }

    @Override
    public JobVacancyBuilder addRequirement(String requirement) {
        if (requirement == null || requirement.trim().isEmpty()) {
            throw new IllegalArgumentException("Requirement cannot be empty");
        }
        if (!tempRequirements.contains(requirement)) {
            tempRequirements.add(requirement);
        }
        return this;
    }

    @Override
    public JobVacancyBuilder withSalary(double salary) {
        if (salary < 0) throw new IllegalArgumentException("Salary cannot be negative");
        jobVacancy.setSalary(salary);
        return this;
    }

    @Override
    public JobVacancy build() {
        // Validaciones finales
        if (jobVacancy.getTitle() == null) {
            throw new IllegalStateException("Title is required");
        }
        if (jobVacancy.getCompany() == null) {
            throw new IllegalStateException("Company is required");
        }

        // Asignar requisitos validados
        jobVacancy.setJobRequirements(new ArrayList<>(tempRequirements));

        // Valor por defecto para fecha si no se especificó
        if (jobVacancy.getPublicationDate() == null) {
            jobVacancy.setPublicationDate(LocalDate.now());
        }

        return jobVacancy;
    }
}