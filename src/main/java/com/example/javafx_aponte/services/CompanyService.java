package com.example.javafx_aponte.services;

import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.repository.CompanyRepository;
import com.example.javafx_aponte.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class CompanyService {
    private final CompanyRepository companyRepo;


    public CompanyService(CompanyRepository companyRepo) {
        this.companyRepo = companyRepo;

    }

    public Company createCompany(Company company) {
       return companyRepo.saveCompany(company);
    }

    public Optional<Company> getCompanyById(int id) {
        return companyRepo.findCompanyById(id);
    }

    public List<Company> getAllCompanies() {
        return companyRepo.findAllCompanies();
    }

    public List<Company> searchCompaniesBySector(String sector) {
        return companyRepo.findCompanyBySector(sector);
    }

    public List<Company> searchCompaniesByName(String name) {
        return companyRepo.findCompanyByNameContaining(name);
    }

    public void deleteCompany(int id) {
        companyRepo.deleteCompanyById(id);
    }

    public Company updateCompany(Company company) {
        return companyRepo.updateCompanyInformation(company);
    }
}
