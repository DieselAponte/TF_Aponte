package com.example.javafx_aponte.builder.directors;
import com.example.javafx_aponte.builder.builders.JobVacancyBuilder;
import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.models.JobVacancy;

import java.time.LocalDate;
import java.util.List;

public class JobVacancyDirector {
    private static final double DEFAULT_BASE_SALARY = 30000.0;

    public JobVacancy createStandardJob(JobVacancyBuilder builder,
                                        String title,
                                        Company company,
                                        List<String> requirements) {
        return builder.reset()
                .withTitle(title)
                .withDescription("Standard position at " + company.getNameCompany())
                .withCompany(company)
                .withRequirements(requirements)
                .withSalary(DEFAULT_BASE_SALARY) // Usamos valor por defecto
                .withPublicationDate(LocalDate.now())
                .build();
    }

    public JobVacancy createQuickJob(JobVacancyBuilder builder,
                                     String title,
                                     Company company) {
        return builder.reset()
                .withTitle(title)
                .withCompany(company)
                .withPublicationDate(LocalDate.now())
                .withSalary(DEFAULT_BASE_SALARY)
                .build();
    }

    public JobVacancy createTechJob(JobVacancyBuilder builder,
                                    String title,
                                    Company company,
                                    List<String> techSkills,
                                    double salary) {
        if (salary < DEFAULT_BASE_SALARY) {
            throw new IllegalArgumentException("Tech positions require higher salary");
        }

        return builder.reset()
                .withTitle(title)
                .withDescription("Tech role requiring: " + String.join(", ", techSkills))
                .withCompany(company)
                .withRequirements(techSkills)
                .withSalary(salary)
                .withPublicationDate(LocalDate.now())
                .build();
    }
}