package com.geldsparenbackend.service;

import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.GroupMemberRepository;
import com.geldsparenbackend.repository.GroupRepository;
import com.geldsparenbackend.repository.SavingGoalRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final SavingGoalRepository savingGoalRepository;
    private final EmailService emailService;

    @Autowired
    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        UserRepository userRepository,
                        SavingGoalRepository savingGoalRepository,
                        EmailService emailService) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.savingGoalRepository = savingGoalRepository;
        this.emailService = emailService;
    }

    public Optional<Group> getGroupBySavingGoalId(Long savingGoalId) {
        return groupRepository.findBySavingGoalId(savingGoalId);
    }

    @Transactional
    public Group createGroupForSavingGoal(Long savingGoalId, String groupName, Long creatorId) {
        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with id: " + savingGoalId));

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorId));

        // التحقق إذا كان هناك مجموعة موجودة مسبقاً
        if (groupRepository.findBySavingGoalId(savingGoalId).isPresent()) {
            throw new RuntimeException("Group already exists for this saving goal");
        }

        Group group = new Group();
        group.setSavingGoal(savingGoal);
        group.setName(groupName);
        group.setCreatedBy(creator);

        Group savedGroup = groupRepository.save(group);

        // إضافة المنشئ كعضو في المجموعة
        GroupMember creatorMember = new GroupMember();
        creatorMember.setGroup(savedGroup);
        creatorMember.setUser(creator);
        creatorMember.setInvitationStatus(GroupMember.InvitationStatus.ACCEPTED);

        groupMemberRepository.save(creatorMember);

        return savedGroup;
    }

    @Transactional
    public GroupMember inviteUserToGroup(Long groupId, String email, Long inviterId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new RuntimeException("Inviter not found with id: " + inviterId));

        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        // التحقق إذا كان المستخدم مدعواً مسبقاً
        Optional<GroupMember> existingInvitation = groupMemberRepository.findByGroupIdAndUserId(groupId, invitedUser.getId());
        if (existingInvitation.isPresent()) {
            throw new RuntimeException("User already invited to this group");
        }

        GroupMember invitation = new GroupMember();
        invitation.setGroup(group);
        invitation.setUser(invitedUser);
        invitation.setInvitationStatus(GroupMember.InvitationStatus.PENDING);

        GroupMember savedInvitation = groupMemberRepository.save(invitation);

        // إرسال بريد إلكتروني بالدعوة
        emailService.sendGroupInvitationEmail(invitedUser.getEmail(), inviter.getUsername(), group.getName());

        return savedInvitation;
    }

    @Transactional
    public GroupMember respondToInvitation(Long groupId, Long userId, boolean accept) {
        GroupMember invitation = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (accept) {
            invitation.setInvitationStatus(GroupMember.InvitationStatus.ACCEPTED);
        } else {
            invitation.setInvitationStatus(GroupMember.InvitationStatus.REJECTED);
        }

        return groupMemberRepository.save(invitation);
    }

    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupId(groupId);
    }

    public List<Group> getUserGroups(Long userId) {
        return groupRepository.findByMemberUserId(userId);
    }

    @Transactional
    public void removeMemberFromGroup(Long groupId, Long userId, Long removerId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        // التحقق إذا كان المستخدم هو المنشئ أو لديه الصلاحية
        if (!group.getCreatedBy().getId().equals(removerId)) {
            throw new RuntimeException("Only group creator can remove members");
        }

        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Member not found in group"));

        groupMemberRepository.delete(member);
    }
}