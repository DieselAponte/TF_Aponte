package com.example.javafx_aponte.builder.products;

import com.example.javafx_aponte.models.Company;
import com.example.javafx_aponte.models.JobVacancy;
import java.time.LocalDate;
import java.util.List;

public class DetailedJobVacancy extends JobVacancy {
    private List<String> benefits;
    private boolean featured;

    public DetailedJobVacancy(int id, String title, String description, Company company, LocalDate publicationDate, List<String> jobRequirements, double salary) {
        super(id, title, description, company, publicationDate, jobRequirements, salary);
    }

    // Getters y setters adicionales
    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
}
