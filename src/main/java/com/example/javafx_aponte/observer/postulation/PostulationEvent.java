package com.example.javafx_aponte.observer.postulation;

import com.example.javafx_aponte.models.Postulation;
import com.example.javafx_aponte.util.PostulationStatus;

public class PostulationEvent {
    private final Postulation postulation;
    private final PostulationStatus previousStatus;

    public PostulationEvent(Postulation postulation, PostulationStatus previousStatus) {
        this.postulation = postulation;
        this.previousStatus = previousStatus;
    }

    // Getters
    public Postulation getPostulation() { return postulation; }
    public PostulationStatus getPreviousStatus() { return previousStatus; }
}
