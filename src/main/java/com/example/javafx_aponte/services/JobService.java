package com.example.javafx_aponte.services;

import com.example.javafx_aponte.models.JobVacancies;
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

    public JobVacancies createJobVacancy(JobVacancies vacancy) {
        companyRepo.findCompanyById(vacancy.getCompany().getIdCompany())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no existe"));
        return jobRepo.saveJobVacancy(vacancy);
    }

    public Optional<JobVacancies> getJobById(int id) {
        return jobRepo.findJobVacancyById(id);
    }

    public List<JobVacancies> getAllJobs() {
        return jobRepo.findAllJobVacancies();
    }

    public List<JobVacancies> getJobsByCompany(int companyId) {
        return jobRepo.findJobVacanciesByCompanyId(companyId);
    }

    public List<JobVacancies> searchJobs(String keyword, String sector, double minSalary, double maxSalary) {
        if (keyword != null && !keyword.isEmpty()) {
            return jobRepo.findJobVacanciesByKeyword(keyword);
        } else if (sector != null && !sector.isEmpty()) {
            return jobRepo.findJobVacanciesByCompanyName(sector);
        } else {
            return jobRepo.findJobVacanciesBySalaryRange(minSalary, maxSalary);
        }
    }

    public List<JobVacancies> getRecommendedJobs(User user) {
        if (user.getProfile() == null || user.getProfile().getJobSkills().isEmpty()) {
            return jobRepo.findAllJobVacancies();
        }
        return jobRepo.findJobVacanciesBySkills(user.getProfile().getJobSkills());
    }

    public void deleteJob(int id) {
        jobRepo.deleteJobVacancy(id);
    }

    public JobVacancies updateJob(JobVacancies vacancy) {
        return jobRepo.updateJobVacancy(vacancy);
    }
}