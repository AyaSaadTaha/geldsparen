package com.geldsparenbackend.controller;

import java.math.BigDecimal;

public class GroupMemberInvitationDTO {
    private Long id;
    private String groupName;
    private String savingGoalName;
    private String invitedBy;
    private BigDecimal monthlyContribution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSavingGoalName() {
        return savingGoalName;
    }

    public void setSavingGoalName(String savingGoalName) {
        this.savingGoalName = savingGoalName;
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(BigDecimal monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }
}
