package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.Profile;
import com.example.javafx_aponte.models.User;

import java.sql.*;
import java.util.*;

public class ConcreteUserRepository implements UserRepository {
    private final Connection connection;
    private final ProfileRepository profileRepository;

    public ConcreteUserRepository(Connection connection) throws SQLException {
        this.connection = connection;
        this.profileRepository = new ConcreteProfileRepository(connection);
    }

    @Override
    public User saveUser(User user) {
        System.out.println("DEBUG - Saving user: " + user);
        String sql = user.getId() == 0 ?
                "INSERT INTO users (username, email, password, profile_id) VALUES (?, ?, ?, ?)" :
                "UPDATE users SET username = ?, email = ?, password = ?, profile_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (user.getId() == 0) {
                // INSERT
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setObject(4, user.getProfile() != null ? user.getProfile().getId() : null);
            } else {
                // UPDATE
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setObject(4, user.getProfile() != null ? user.getProfile().getId() : null);
                stmt.setInt(5, user.getId());
            }

            stmt.executeUpdate();

            if (user.getId() == 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user: " + e.getMessage(), e); // Mostrar mensaje completo
        }
        return user;
    }

    @Override
    public Optional<User> findUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";  // Cambiado de id a user_id
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),  // Cambiado de id a user_id
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        null // profile se establece después
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all users", e);
        }
        return users;
    }

    @Override
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public User updateUser(User user) {
        return saveUser(user);
    }

    @Override
    public boolean authenticateUser(String email, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error authenticating user", e);
        }
        return false;
    }

    @Override
    public List<User> findUserByUsername(String username) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + username + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding users by username", e);
        }
        return users;
    }

    private User mapToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Optional<Profile> profile = profileRepository.findByUserId(id);

        return new User(
                id,
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                profile.orElse(null)
        );
    }
}