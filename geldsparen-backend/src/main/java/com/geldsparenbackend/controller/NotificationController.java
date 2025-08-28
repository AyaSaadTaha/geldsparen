package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.Notification;
import com.geldsparenbackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // الحصول على جميع إشعارات المستخدم
    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(Authentication authentication) {
        String username = authentication.getName();
        List<Notification> notifications = notificationService.getUserNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    // الحصول على الإشعارات غير المقروءة
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Authentication authentication) {
        String username = authentication.getName();
        List<Notification> notifications = notificationService.getUnreadNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    // الحصول على عدد الإشعارات غير المقروءة
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        String username = authentication.getName();
        Long count = notificationService.getUnreadCount(username);
        return ResponseEntity.ok(count);
    }

    // الحصول على الإشعارات حسب النوع
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Notification>> getNotificationsByType(
            @PathVariable String type, Authentication authentication) {
        String username = authentication.getName();
        List<Notification> notifications = notificationService.getNotificationsByType(username, type);
        return ResponseEntity.ok(notifications);
    }

    // تحديد إشعار كمقروء
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        notificationService.markAsRead(id, username);
        return ResponseEntity.ok().build();
    }

    // تحديد جميع الإشعارات كمقروءة
    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Authentication authentication) {
        String username = authentication.getName();
        notificationService.markAllAsRead(username);
        return ResponseEntity.ok().build();
    }

    // حذف إشعار
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        notificationService.deleteNotification(id, username);
        return ResponseEntity.ok().build();
    }

    // إنشاء إشعار جديد (للتطبيقات الإدارية)
    @PostMapping
    public ResponseEntity<Notification> createNotification(
            @RequestBody Notification notification, Authentication authentication) {
        String username = authentication.getName();

        // في التطبيق الحقيقي، قد تريد التحقق من الصلاحيات هنا
        // للتأكد من أن المستخدم لديه صلاحية إنشاء إشعارات

        Notification createdNotification = notificationService.createNotification(notification);
        return ResponseEntity.ok(createdNotification);
    }

    // تنظيف الإشعارات القديمة (للتطبيقات الإدارية)
    @DeleteMapping("/clean/{daysOld}")
    public ResponseEntity<?> cleanOldNotifications(
            @PathVariable int daysOld, Authentication authentication) {
        String username = authentication.getName();

        // في التطبيق الحقيقي، قد تريد التحقق من أن المستخدم هو مدير
        notificationService.cleanOldNotifications(daysOld);
        return ResponseEntity.ok().build();
    }
}