package com.example.javafx_aponte.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Notification {
    private int id;
    private int userId;
    private String titleMessage;
    private String textMessage;
    private LocalDateTime sendingDate;
    private boolean read;

    public Notification(int id, int userId, String titleMessage, String textMessage, LocalDateTime sendingDate, boolean read) {
        this.id = id;
        this.userId = userId;
        this.titleMessage = titleMessage;
        this.textMessage = textMessage;
        this.sendingDate = sendingDate;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void markAsRead(){
        this.read = true;
    }

    public String getTitleMessage() {
        return titleMessage;
    }

    public void setTitleMessage(String titleMessage) {
        this.titleMessage = titleMessage;
    }

    public String getTextMessage() {
        return textMessage;
    }


    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public LocalDateTime getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(LocalDateTime sendingDate) {
        this.sendingDate = sendingDate;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }



    @Override
    public String toString() {
        return String.format("%s - %s", getTitleMessage(), getTextMessage());
    }
}
