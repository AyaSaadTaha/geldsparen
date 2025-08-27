package com.geldsparenbackend.service;

import com.geldsparenbackend.model.Notification;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.NotificationRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // إزالة dependency على MonthlyPaymentService
    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public Integer getUnreadNotificationCount(Long userId) {
        return notificationRepository.countUnreadNotificationsByUserId(userId);
    }

    public Notification createNotification(Long userId, String title, String message, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);

        return notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    // إزالة المهام المجدولة التي تعتمد على MonthlyPaymentService
    // سيتم نقل هذه المهام إلى خدمة منفصلة
}