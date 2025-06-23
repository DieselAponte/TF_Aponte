package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.JobVacancy;

import java.util.List;
import java.util.Optional;

public interface JobVacancyRepository {
    JobVacancy saveJobVacancy(JobVacancy jobVacancy);
    Optional<JobVacancy> findJobVacancyById(Integer id);
    List<JobVacancy> findAllJobVacancies();
    List<JobVacancy> findJobVacanciesByCompanyId(int companyId);
    List<JobVacancy> findJobVacanciesByCompanyName(String companyName);
    List<JobVacancy> findJobVacanciesByKeyword(String keyword);
    List<JobVacancy> findJobVacanciesBySalaryRange(double min, double max);
    void deleteJobVacancy(int id);
    JobVacancy updateJobVacancy(JobVacancy vacancy);
    List<JobVacancy> findJobVacanciesBySkills(List<String> skills);
}
