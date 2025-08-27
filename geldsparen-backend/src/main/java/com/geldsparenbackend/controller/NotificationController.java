package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.Notification;
import com.geldsparenbackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getUserNotifications(@AuthenticationPrincipal Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    @GetMapping("/unread")
    public List<Notification> getUnreadNotifications(@AuthenticationPrincipal Long userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    @GetMapping("/unread-count")
    public Integer getUnreadNotificationCount(@AuthenticationPrincipal Long userId) {
        return notificationService.getUnreadNotificationCount(userId);
    }

    @PostMapping("/mark-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}