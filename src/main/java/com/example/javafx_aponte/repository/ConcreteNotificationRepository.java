package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.Notification;
import com.example.javafx_aponte.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConcreteNotificationRepository implements NotificationRepository {
    private final Connection connection;

    public ConcreteNotificationRepository(Connection connection) throws SQLException {
        this.connection = connection;
    }

    @Override
    public Notification saveNotification(Notification notification) {
        String sql = notification.getId() == 0 ?
                "INSERT INTO notifications (user_id, title, message, created_at, is_read) VALUES (?, ?, ?, ?, ?)" :
                "UPDATE notifications SET user_id = ?, title = ?, message = ?, is_read = ?, read_at = ? WHERE notification_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (notification.getId() == 0) {
                // INSERT
                stmt.setInt(1, notification.getUserId());
                stmt.setString(2, notification.getTitleMessage());
                stmt.setString(3, notification.getTextMessage());
                stmt.setTimestamp(4, Timestamp.valueOf(notification.getSendingDate()));
                stmt.setBoolean(5, notification.isRead());
            } else {
                // UPDATE
                stmt.setInt(1, notification.getUserId());
                stmt.setString(2, notification.getTitleMessage());
                stmt.setString(3, notification.getTextMessage());
                stmt.setBoolean(4, notification.isRead());
                stmt.setTimestamp(5, notification.isRead() ? Timestamp.valueOf(LocalDateTime.now()) : null);
                stmt.setInt(6, notification.getId());
            }

            stmt.executeUpdate();

            if (notification.getId() == 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        notification.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving notification: " + e.getMessage(), e);
        }
        return notification;
    }

    @Override
    public List<Notification> findNotificationByUser(User user) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY sending_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapToNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notifications", e);
        }
        return notifications;
    }

    @Override
    public List<Notification> findNotificationUnreadByUser(User user) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = false ORDER BY sending_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notifications.add(mapToNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching unread notifications", e);
        }
        return notifications;
    }

    @Override
    public void markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error marking notification as read", e);
        }
    }

    @Override
    public void markAllNotificationsAsRead(User user) {
        String sql = "UPDATE notifications SET is_read = true WHERE user_id = ? AND is_read = false";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error marking all notifications as read", e);
        }
    }

    @Override
    public void deleteNotification(int id) {
        String sql = "DELETE FROM notifications WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting notification", e);
        }
    }

    @Override
    public int countUnreadNotifications(User user) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = false";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting unread notifications", e);
        }
        return 0;
    }

    private Notification mapToNotification(ResultSet rs) throws SQLException {
        return new Notification(
                rs.getInt("notification_id"), // Cambiado de "id"
                rs.getInt("user_id"),
                rs.getString("title"),
                rs.getString("message"),
                rs.getTimestamp("created_at").toLocalDateTime(), // Cambiado de "sending_date"
                rs.getBoolean("is_read")
        );
    }
}