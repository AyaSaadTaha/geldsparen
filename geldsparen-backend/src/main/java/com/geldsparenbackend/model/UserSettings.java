package com.geldsparenbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "notification_enabled")
    private Boolean notificationEnabled = true;

    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;

    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;

    @Column(length = 10)
    private String language = "ar";

    @Column(length = 3)
    private String currency = "EUR";

    @Column(length = 20)
    private String theme = "light";

    @Column(name = "monthly_budget_notification")
    private Boolean monthlyBudgetNotification = true;

    @Column(name = "payment_reminder_notification")
    private Boolean paymentReminderNotification = true;

    @Column(name = "low_balance_alert")
    private Boolean lowBalanceAlert = true;

    @Column(name = "alert_threshold", precision = 10, scale = 2)
    private BigDecimal alertThreshold = new BigDecimal("100.00");

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // طريقة مساعدة للتحقق من إعدادات الإشعارات
    public boolean shouldSendEmailNotification() {
        return Boolean.TRUE.equals(notificationEnabled) && Boolean.TRUE.equals(emailNotifications);
    }

    public boolean shouldSendPushNotification() {
        return Boolean.TRUE.equals(notificationEnabled) && Boolean.TRUE.equals(pushNotifications);
    }

    public boolean shouldSendPaymentReminder() {
        return Boolean.TRUE.equals(notificationEnabled) && Boolean.TRUE.equals(paymentReminderNotification);
    }
}