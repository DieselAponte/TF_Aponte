package com.example.javafx_aponte.observer.job_alerts;

public interface JobAlertObserver {
    void onNewJobPosted(JobVacancyEvent event);
}
