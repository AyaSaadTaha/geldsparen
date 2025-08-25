package com.geldsparenbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_members")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus invitationStatus = InvitationStatus.PENDING;

    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (invitationStatus == InvitationStatus.ACCEPTED && joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    public enum InvitationStatus {
        PENDING, ACCEPTED, REJECTED
    }
}