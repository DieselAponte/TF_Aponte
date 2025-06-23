package com.example.javafx_aponte.models;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private Profile profile;

    public User(int id, String name, String email, String password, Profile profile) {
        this.id = id;
        this.username = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public boolean authenticate(String inputEmail, String inputPassword) {
        return this.email.equals(inputEmail) && this.password.equals(inputPassword);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}

