package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.service.GroupMemberService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group-members")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    @GetMapping("/saving-goal/{savingGoalId}")
    public ResponseEntity<List<GroupMember>> getGroupMembersBySavingGoal(
            @PathVariable Long savingGoalId, Authentication authentication) {
        String username = authentication.getName();
        List<GroupMember> members = groupMemberService.getGroupMembersBySavingGoalId(savingGoalId, username);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/saving-goal/{savingGoalId}/contributions")
    public ResponseEntity<Map<String, BigDecimal>> getMemberContributions(
            @PathVariable Long savingGoalId, Authentication authentication) {
        String username = authentication.getName();
        Map<String, BigDecimal> contributions = groupMemberService.getMemberContributions(savingGoalId, username);
        return ResponseEntity.ok(contributions);
    }
}
