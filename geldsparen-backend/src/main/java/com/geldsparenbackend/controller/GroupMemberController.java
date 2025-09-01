package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.service.GroupMemberService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/invitations")
    public ResponseEntity<List<GroupMemberInvitationDTO>> getPendingInvitations(Authentication authentication) {
        String username = authentication.getName();
        List<GroupMemberInvitationDTO> invitations = groupMemberService.getPendingInvitations(username);
        return ResponseEntity.ok(invitations);
    }

    @GetMapping("/invitations/count")
    public ResponseEntity<Map<String, Long>> countPendingInvitations(Authentication authentication) {
        String username = authentication.getName();
        Long count = groupMemberService.countPendingInvitations(username);
        return ResponseEntity.ok(Collections.singletonMap("count", count));
    }

    @PatchMapping("/{groupMemberId}/respond")
    public ResponseEntity<?> respondToGroupInvitation(@PathVariable Long groupMemberId, @RequestBody Map<String, String> request, Authentication authentication) {
        String username = authentication.getName();
        String response = request.get("response");

        GroupMember.InvitationStatus status;
        try {
            status = GroupMember.InvitationStatus.valueOf(response.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid response status");
        }

        groupMemberService.respondToGroupInvitation(groupMemberId, status, username);
        return ResponseEntity.ok().build();
    }
}
