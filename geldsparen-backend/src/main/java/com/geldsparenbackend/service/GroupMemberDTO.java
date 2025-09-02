package com.geldsparenbackend.service;

import com.geldsparenbackend.model.GroupMember;

import java.time.LocalDateTime;

public class GroupMemberDTO {
    private Long id;
    private String email;
    private String username; // if user exists
    private GroupMember.InvitationStatus invitationStatus;
    private LocalDateTime joinedAt;
    private LocalDateTime createdAt;

    // Constructor from GroupMember entity
    public GroupMemberDTO(GroupMember member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.invitationStatus = member.getInvitationStatus();
        this.joinedAt = member.getJoinedAt();
        this.createdAt = member.getCreatedAt();

        if (member.getUser() != null) {
            this.username = member.getUser().getUsername();
        }
    }

    // Getters and setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public GroupMember.InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(GroupMember.InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}