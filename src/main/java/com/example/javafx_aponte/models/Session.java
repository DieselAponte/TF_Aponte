package com.example.javafx_aponte.models;

public class Session {
    private static User currentUser;

    public static void starSession(User user){
        currentUser = user;
    }

    public static void closeSession(){
        currentUser = null;
    }

    public static User getCurrentUser(){
        return currentUser;
    }
}
