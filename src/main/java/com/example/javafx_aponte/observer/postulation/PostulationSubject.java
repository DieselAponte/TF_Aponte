package com.example.javafx_aponte.observer.postulation;

import java.util.ArrayList;
import java.util.List;

public abstract class PostulationSubject {
    private final List<PostulationObserver> observers = new ArrayList<>();

    public void addObserver(PostulationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PostulationObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(PostulationEvent event) {
        observers.forEach(observer -> observer.update(event));
    }
}
