package com.example.javafx_aponte.repository;

import com.example.javafx_aponte.models.Notification;
import com.example.javafx_aponte.models.User;

import java.util.List;

public interface NotificationRepository {
    Notification saveNotification(Notification notification);
    List<Notification> findNotificationByUser(User user);
    List<Notification> findNotificationUnreadByUser(User user);
    void markNotificationAsRead(int notificationId);
    void markAllNotificationsAsRead(User user);
    void deleteNotification(int id);
    int countUnreadNotifications(User user);
}
