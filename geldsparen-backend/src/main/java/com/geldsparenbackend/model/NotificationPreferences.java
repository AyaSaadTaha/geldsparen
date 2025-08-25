package com.geldsparenbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "preference_type", nullable = false, length = 50)
    private String preferenceType;

    private Boolean enabled = true;

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

    //Präferenz typen
    public enum PreferenceType {
        PAYMENT_REMINDERS(" Zahlungs erinnerungen"),
        BUDGET_ALERTS(" Budget benachrichtigungen"),
        GROUP_INVITATIONS("Gruppen einladungen"),
        EXPENSE_NOTIFICATIONS("Ausgaben benachrichtigungen"),
        SAVING_GOAL_UPDATES("  Sparziel-Updates");

        private final String description;

        PreferenceType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}