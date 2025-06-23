package com.example.javafx_aponte.services;

import com.example.javafx_aponte.builder.builders.JobVacancyBuilder;
import com.example.javafx_aponte.builder.directors.JobVacancyDirector;
import com.example.javafx_aponte.builder.products.ConcreteJobVacancyBuilder;
import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.models.JobVacancy;
import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.observer.job_alerts.JobAlertSubject;
import com.example.javafx_aponte.observer.job_alerts.JobVacancyEvent;
import com.example.javafx_aponte.repository.CompanyRepository;
import com.example.javafx_aponte.repository.JobVacancyRepository;

import java.util.List;
import java.util.Optional;

public class JobService extends JobAlertSubject {
    private final JobVacancyRepository jobRepo;
    private final CompanyRepository companyRepo;
    private final JobVacancyDirector director;

    public JobService(JobVacancyRepository jobRepo, CompanyRepository companyRepo) {
        this.jobRepo     = jobRepo;
        this.companyRepo = companyRepo;
        this.director    = new JobVacancyDirector();
    }

    public JobVacancy createStandardJob(String title, int companyId, List<String> requirements) {
        Company company = companyRepo.findCompanyById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no existe"));

        JobVacancyBuilder builder = new ConcreteJobVacancyBuilder();
        JobVacancy vacancy = director.createStandardJob(builder, title, company, requirements);

        JobVacancy saved = jobRepo.saveJobVacancy(vacancy);
        // notify observers of the new vacancy
        notifyObservers(new JobVacancyEvent(saved));
        return saved;
    }

    public JobVacancy createQuickJob(String title, int companyId) {
        Company company = companyRepo.findCompanyById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no existe"));

        JobVacancyBuilder builder = new ConcreteJobVacancyBuilder();
        JobVacancy vacancy = director.createQuickJob(builder, title, company);

        JobVacancy saved = jobRepo.saveJobVacancy(vacancy);
        notifyObservers(new JobVacancyEvent(saved));
        return saved;
    }

    public JobVacancy createTechJob(String title, int companyId, List<String> techSkills, double salary) {
        Company company = companyRepo.findCompanyById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no existe"));

        JobVacancyBuilder builder = new ConcreteJobVacancyBuilder();
        JobVacancy vacancy = director.createTechJob(builder, title, company, techSkills, salary);

        JobVacancy saved = jobRepo.saveJobVacancy(vacancy);
        notifyObservers(new JobVacancyEvent(saved));
        return saved;
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
