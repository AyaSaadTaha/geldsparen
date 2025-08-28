package com.geldsparenbackend.service;

import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.model.GroupMember.InvitationStatus;
import com.geldsparenbackend.repository.GroupRepository;
import com.geldsparenbackend.repository.GroupMemberRepository;
import com.geldsparenbackend.repository.SavingGoalRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    public Optional<Group> getGroupBySavingGoalId(Long savingGoalId, String username) {
        User user = userService.findByUsername(username);
        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // التحقق من أن هدف التوفير ينتمي للمستخدم المصادق
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to access this group");
        }

        return groupRepository.findBySavingGoalId(savingGoalId);
    }

    @Transactional
    public Group createGroupForSavingGoal(Long savingGoalId, String groupName, String username) {
        User user = userService.findByUsername(username);
        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // التحقق من أن هدف التوفير ينتمي للمستخدم المصادق
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to create a group for this saving goal");
        }

        // التحقق من عدم وجود مجموعة لهذا الهدف بالفعل
        if (groupRepository.existsBySavingGoalId(savingGoalId)) {
            throw new RuntimeException("A group already exists for this saving goal");
        }

        // إنشاء المجموعة
        Group group = new Group();
        group.setSavingGoal(savingGoal);
        group.setName(groupName);
        group.setCreatedBy(user);

        Group savedGroup = groupRepository.save(group);

        // إضافة المنشئ كعضو في المجموعة
        GroupMember creatorMember = new GroupMember();
        creatorMember.setGroup(savedGroup);
        creatorMember.setUser(user);
        creatorMember.setInvitationStatus(InvitationStatus.ACCEPTED);
        groupMemberRepository.save(creatorMember);

        return savedGroup;
    }

    @Transactional
    public Group addMemberToGroup(Long groupId, String memberEmail, String username) {
        User currentUser = userService.findByUsername(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // التحقق من أن المستخدم الحالي هو منشئ المجموعة
        if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the group creator can add members");
        }

        // البحث عن المستخدم بالإيميل
        User memberUser = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + memberEmail));

        // التحقق من عدم وجود المستخدم في المجموعة بالفعل
        if (groupMemberRepository.existsByGroupAndUser(group, memberUser)) {
            throw new RuntimeException("User is already a member of this group");
        }

        // إضافة المستخدم كعضو في المجموعة
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(memberUser);
        member.setInvitationStatus(InvitationStatus.PENDING);
        groupMemberRepository.save(member);

        // إرسال إشعار دعوة للمستخدم
        notificationService.sendGroupInvitation(memberUser, group, currentUser);

        return group;
    }

    @Transactional
    public Group removeMemberFromGroup(Long groupId, Long memberId, String username) {
        User currentUser = userService.findByUsername(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // التحقق من أن المستخدم الحالي هو منشئ المجموعة
        if (!group.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the group creator can remove members");
        }

        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Group member not found"));

        // منع حذف المنشئ من المجموعة
        if (member.getUser().getId().equals(group.getCreatedBy().getId())) {
            throw new RuntimeException("Cannot remove the group creator");
        }

        groupMemberRepository.delete(member);

        return group;
    }

    @Transactional
    public void respondToGroupInvitation(Long groupMemberId, GroupMember.InvitationStatus response, String username) {
        User user = userService.findByUsername(username);
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new RuntimeException("Group invitation not found"));

        // التحقق من أن الدعوة موجهة للمستخدم المصادق
        if (!groupMember.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("This invitation is not for you");
        }

        groupMember.setInvitationStatus(response);
        groupMemberRepository.save(groupMember);

        // إرسال إشعار بقرار الدعوة
        notificationService.sendGroupInvitationResponse(groupMember, response);
    }

    public List<GroupMember> getGroupMembers(Long groupId, String username) {
        User user = userService.findByUsername(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // التحقق من أن المستخدم عضو في المجموعة
        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("You are not a member of this group");
        }

        return groupMemberRepository.findByGroup(group);
    }

    @Transactional
    public void deleteGroup(Long groupId, String username) {
        User user = userService.findByUsername(username);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // التحقق من أن المستخدم الحالي هو منشئ المجموعة
        if (!group.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Only the group creator can delete the group");
        }

        // حذف جميع الأعضاء أولاً
        groupMemberRepository.deleteByGroup(group);

        // ثم حذف المجموعة
        groupRepository.delete(group);
    }
}