package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.JobVacancies;

import java.util.List;
import java.util.Optional;

public interface JobVacancyRepository {
    JobVacancies saveJobVacancy(JobVacancies jobVacancies);
    Optional<JobVacancies> findJobVacancyById(Integer id);
    List<JobVacancies> findAllJobVacancies();
    List<JobVacancies> findJobVacanciesByCompanyId(int companyId);
    List<JobVacancies> findJobVacanciesByCompanyName(String companyName);
    List<JobVacancies> findJobVacanciesByKeyword(String keyword);
    List<JobVacancies> findJobVacanciesBySalaryRange(double min, double max);
    void deleteJobVacancy(int id);
    JobVacancies updateJobVacancy(JobVacancies vacancy);
    List<JobVacancies> findJobVacanciesBySkills(List<String> skills);
}
