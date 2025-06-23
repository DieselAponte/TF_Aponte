package com.example.javafx_aponte.models;

public class Company {
    private int idCompany;
    private String nameCompany;
    private String description;
    private String jobSector;
    private String companyAddress;

    public Company(int idCompany, String nameCompany, String description, String jobSector, String companyAddress) {
        this.idCompany = idCompany;
        this.nameCompany = nameCompany;
        this.description = description;
        this.jobSector = jobSector;
        this.companyAddress = companyAddress;
    }

    public int getIdCompany() {
        return idCompany;
    }

    public void setIdCompany(int idCompany) {
        this.idCompany = idCompany;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany = nameCompany;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobSector() {
        return jobSector;
    }

    public void setJobSector(String jobSector) {
        this.jobSector = jobSector;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}
