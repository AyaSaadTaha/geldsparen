package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.service.GroupSavingGoalRequest;
import com.geldsparenbackend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/saving-goals/group")
    public ResponseEntity<Group> createGroupSavingGoal(
            @RequestBody GroupSavingGoalRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        Group group = groupService.createGroupSavingGoal(request, username);

        return ResponseEntity.ok(group);
    }

    @GetMapping("/saving-goal/{savingGoalId}")
    public ResponseEntity<Group> getGroupBySavingGoalId(
            @PathVariable Long savingGoalId, Authentication authentication) {
        String username = authentication.getName();
        Optional<Group> group = groupService.getGroupBySavingGoalId(savingGoalId, username);

        return group.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Group> addMemberToGroup(
            @PathVariable Long groupId,
            @RequestParam String memberEmail,
            Authentication authentication) {
        String username = authentication.getName();
        Group group = groupService.addMemberToGroup(groupId, memberEmail, username);
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Group> removeMemberFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            Authentication authentication) {
        String username = authentication.getName();
        Group group = groupService.removeMemberFromGroup(groupId, memberId, username);
        return ResponseEntity.ok(group);
    }

    @PatchMapping("/invitations/{groupMemberId}")
    public ResponseEntity<?> respondToGroupInvitation(
            @PathVariable Long groupMemberId,
            @RequestParam GroupMember.InvitationStatus response, // تم التصحيح هنا
            Authentication authentication) {
        String username = authentication.getName();
        groupService.respondToGroupInvitation(groupMemberId, response, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{savingGoalId}/members")
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable Long savingGoalId, Authentication authentication) {
        String username = authentication.getName();
        List<GroupMember> members = groupService.getGroupMembers(savingGoalId, username);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @PathVariable Long groupId, Authentication authentication) {
        String username = authentication.getName();
        groupService.deleteGroup(groupId, username);
        return ResponseEntity.ok().build();
    }
}