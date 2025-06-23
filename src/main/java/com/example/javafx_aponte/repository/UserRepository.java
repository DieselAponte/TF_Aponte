package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User saveUser(User user);
    Optional<User> findUserById(int id);
    List<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    List<User> findAllUsers();
    void deleteUser(int id);
    User updateUser(User user);
    boolean authenticateUser(String email, String password);
}
