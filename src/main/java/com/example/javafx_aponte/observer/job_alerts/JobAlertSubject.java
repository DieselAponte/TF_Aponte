package com.example.javafx_aponte.observer.job_alerts;

import java.util.ArrayList;
import java.util.List;

public abstract class JobAlertSubject {
    private final List<JobAlertObserver> observers = new ArrayList<>();

    public void addObserver(JobAlertObserver observer) {
        observers.add(observer);
    }

    protected void notifyObservers(JobVacancyEvent event) {
        observers.forEach(observer -> observer.onNewJobPosted(event));
    }
}