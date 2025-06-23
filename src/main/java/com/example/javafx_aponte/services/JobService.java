package com.example.javafx_aponte.services;

import com.example.javafx_aponte.models.JobVacancy;
import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.repository.CompanyRepository;
import com.example.javafx_aponte.repository.JobVacancyRepository;

import java.util.List;
import java.util.Optional;

public class JobService {
    private final JobVacancyRepository jobRepo;
    private final CompanyRepository companyRepo;

    public JobService(JobVacancyRepository jobRepo, CompanyRepository companyRepo) {
        this.jobRepo = jobRepo;
        this.companyRepo = companyRepo;
    }

    public JobVacancy createJobVacancy(JobVacancy vacancy) {
        companyRepo.findCompanyById(vacancy.getCompany().getIdCompany())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no existe"));
        return jobRepo.saveJobVacancy(vacancy);
    }

    public Optional<JobVacancy> getJobById(int id) {
        return jobRepo.findJobVacancyById(id);
    }

    public List<JobVacancy> getAllJobs() {
        return jobRepo.findAllJobVacancies();
    }

    public List<JobVacancy> getJobsByCompany(int companyId) {
        return jobRepo.findJobVacanciesByCompanyId(companyId);
    }

    public List<JobVacancy> searchJobs(String keyword, String sector, double minSalary, double maxSalary) {
        if (keyword != null && !keyword.isEmpty()) {
            return jobRepo.findJobVacanciesByKeyword(keyword);
        } else if (sector != null && !sector.isEmpty()) {
            return jobRepo.findJobVacanciesByCompanyName(sector);
        } else {
            return jobRepo.findJobVacanciesBySalaryRange(minSalary, maxSalary);
        }
    }

    public List<JobVacancy> getRecommendedJobs(User user) {
        if (user.getProfile() == null || user.getProfile().getJobSkills().isEmpty()) {
            return jobRepo.findAllJobVacancies();
        }
        return jobRepo.findJobVacanciesBySkills(user.getProfile().getJobSkills());
    }

    public void deleteJob(int id) {
        jobRepo.deleteJobVacancy(id);
    }

    public JobVacancy updateJob(JobVacancy vacancy) {
        return jobRepo.updateJobVacancy(vacancy);
    }
}