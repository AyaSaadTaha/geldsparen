package com.geldsparenbackend.service;

import com.geldsparenbackend.model.SavingGoal;
import java.util.List;

public class GroupSavingGoalRequest {
    private SavingGoal savingGoal;
    private String groupName;
    private List<String> memberEmails;

    // Constructors
    public GroupSavingGoalRequest() {}

    public GroupSavingGoalRequest(SavingGoal savingGoal, String groupName, List<String> memberEmails) {
        this.savingGoal = savingGoal;
        this.groupName = groupName;
        this.memberEmails = memberEmails;
    }

    // Getters and setters
    public SavingGoal getSavingGoal() { return savingGoal; }
    public void setSavingGoal(SavingGoal savingGoal) { this.savingGoal = savingGoal; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public List<String> getMemberEmails() { return memberEmails; }
    public void setMemberEmails(List<String> memberEmails) { this.memberEmails = memberEmails; }
}