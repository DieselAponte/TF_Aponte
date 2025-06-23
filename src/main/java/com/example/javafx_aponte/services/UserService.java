package com.example.javafx_aponte.services;

import com.example.javafx_aponte.models.User;
import com.example.javafx_aponte.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User registerUser(User user) {
        if (userRepo.findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        return userRepo.saveUser(user);
    }

    public Optional<User> getUserById(int id) {
        return userRepo.findUserById(id);
    }

    public List<User> searchUsersByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepo.findAllUsers();
    }

    public void deleteUser(int id) {
        userRepo.deleteUser(id);
    }


    public User updateUser(User user) {
        return userRepo.updateUser(user);
    }

    public User authenticate(String email, String password) {
        if (userRepo.authenticateUser(email, password)) {
            return userRepo.findUserByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Error de autenticación"));
        }
        throw new SecurityException("Credenciales inválidas");
    }
}