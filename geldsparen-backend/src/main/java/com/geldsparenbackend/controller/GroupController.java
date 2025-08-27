package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/{savingGoalId}")
    public ResponseEntity<Group> createGroup(@PathVariable Long savingGoalId,
                                             @RequestParam String groupName,
                                             @AuthenticationPrincipal Long userId) {
        try {
            Group group = groupService.createGroupForSavingGoal(savingGoalId, groupName, userId);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{groupId}/invite")
    public ResponseEntity<GroupMember> inviteToGroup(@PathVariable Long groupId,
                                                     @RequestParam String email,
                                                     @AuthenticationPrincipal Long userId) {
        try {
            GroupMember invitation = groupService.inviteUserToGroup(groupId, email, userId);
            return ResponseEntity.ok(invitation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{groupId}/respond")
    public ResponseEntity<GroupMember> respondToInvitation(@PathVariable Long groupId,
                                                           @RequestParam boolean accept,
                                                           @AuthenticationPrincipal Long userId) {
        try {
            GroupMember response = groupService.respondToInvitation(groupId, userId, accept);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{groupId}/members")
    public List<GroupMember> getGroupMembers(@PathVariable Long groupId) {
        return groupService.getGroupMembers(groupId);
    }

    @GetMapping("/my-groups")
    public List<Group> getUserGroups(@AuthenticationPrincipal Long userId) {
        return groupService.getUserGroups(userId);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long groupId,
                                             @PathVariable Long memberId,
                                             @AuthenticationPrincipal Long userId) {
        try {
            groupService.removeMemberFromGroup(groupId, memberId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}