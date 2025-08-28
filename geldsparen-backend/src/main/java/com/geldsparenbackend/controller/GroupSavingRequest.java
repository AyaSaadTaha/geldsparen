package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.SavingGoal;

import java.util.List;

class GroupSavingRequest {
    private SavingGoal savingGoal;
    private List<String> memberEmails;
    private String groupName;

    // Getters and setters
    public SavingGoal getSavingGoal() { return savingGoal; }
    public void setSavingGoal(SavingGoal savingGoal) { this.savingGoal = savingGoal; }
    public List<String> getMemberEmails() { return memberEmails; }
    public void setMemberEmails(List<String> memberEmails) { this.memberEmails = memberEmails; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}