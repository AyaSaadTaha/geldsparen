package com.geldsparenbackend.service;

import com.geldsparenbackend.model.*;
import com.geldsparenbackend.model.GroupMember.InvitationStatus;
import com.geldsparenbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    private SavingGoalService savingGoalService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SpendingPatternRepository spendingPatternRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Transactional
    public Group createGroupSavingGoal(GroupSavingGoalRequest request, String username) {
        // 1. First check if the creator can afford their share
        User createdBy = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SavingGoal savingGoal = request.getSavingGoal();
        //savingGoal.calculateMonthlyAmount();
        Group savedGroup;
        String warningMessage = null;

        // Calculate months between including both start and end months
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = savingGoal.getDeadline();

        // More accurate month calculation that includes both start and end months
        long monthsBetween = calculateTotalMonths(startDate, endDate);

        BigDecimal monthlyAmount = savingGoal.getTargetAmount()
                .divide(BigDecimal.valueOf(monthsBetween), 2, RoundingMode.HALF_UP);

        // For group goals, divide by number of members (including creator)
        int memberCount = request.getMemberEmails().size() + 1; // +1 for creator

        BigDecimal creatorMonthlyContribution = monthlyAmount
                .divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP);


        // Check if creator can afford their share
        Optional<SpendingPattern> spendingPattern = spendingPatternRepository.findByUser(createdBy);
        Optional<CurrentAccount> currentAccount = currentAccountRepository.findByUser(createdBy);

        if (spendingPattern.isPresent() && currentAccount.isPresent()) {
            BigDecimal availableSavings = spendingPattern.get().getSavings();

            if (availableSavings.compareTo(creatorMonthlyContribution) < 0) {
                warningMessage = "Ihre verfügbaren Ersparnisse (€" + availableSavings +
                        ") reichen nicht für die monatliche Zahlung von €" + savingGoal.getMonthlyAmount() +
                        ". Sie müssen Ihre Ausgaben reduzieren oder Ihr Einkommen erhöhen.";
                System.out.println(warningMessage);
            }
        }

            // 1. Saving Goal erstellen
            savingGoal.setMonthlyAmount(creatorMonthlyContribution);

        savingGoal.setTotal_monthly_amount(
                creatorMonthlyContribution.multiply(BigDecimal.valueOf(monthsBetween))
        );

        savingGoal.setUser(createdBy);
        savingGoalRepository.save(savingGoal);

        Group group = new Group();
            group.setName(request.getGroupName());
            group.setSavingGoal(savingGoal);
            group.setCreatedBy(createdBy);
            group.setMembers(new ArrayList<>());

             savedGroup = groupRepository.save(group);

            // 3. Mitglieder hinzufügen und Einladungen senden
            if (request.getMemberEmails() != null && !request.getMemberEmails().isEmpty()) {
                for (String email : request.getMemberEmails()) {
                    if (email != null && !email.trim().isEmpty()) {
                        try {
                            // Benutzer per Email finden
                            Optional<User> memberUser = userService.getUserByEmail(email);

                            GroupMember member = new GroupMember();
                            member.setGroup(savedGroup);
                            member.setEmail(email);

                            if (memberUser.isPresent()) {
                                member.setUser(memberUser.get());
                            } else {
                                // Wenn Benutzer nicht existiert, nur Email speichern
                                member.setUser(null); // oder Sie können einen Platzhalter-Benutzer erstellen
                            }

                            member.setInvitationStatus(InvitationStatus.PENDING);
                            groupMemberRepository.save(member);

                            // Einladungs-Email senden
                            CompletableFuture.runAsync(() -> {
                                try {
                                    emailService.sendGroupInvitation(email, savedGroup.getName(),
                                            createdBy.getUsername(), savingGoal.getName());
                                } catch (Exception e) {
                                }
                            });

                        } catch (Exception e) {
                            // Fehler protokollieren, aber fortfahren
                        }
                    }
                }
            }

        return savedGroup;
    }

    // Helper method for accurate month calculation
    private long calculateTotalMonths(LocalDate startDate, LocalDate endDate) {
        // Calculate inclusive months (both start and end months count)
        long months = ChronoUnit.MONTHS.between(
                startDate.withDayOfMonth(1),
                endDate.withDayOfMonth(1)
        ) + 1; // Add 1 to include both months

        // Ensure at least 1 month
        return Math.max(1, months);
    }

    public Optional<Group> getGroupBySavingGoalId(Long savingGoalId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // التحقق من أن هدف التوفير ينتمي للمستخدم المصادق
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to access this group");
        }

        return groupRepository.findBySavingGoalId(savingGoalId);
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
    public void respondToGroupInvitation(Long groupMemberId, InvitationStatus response, String username) {
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

    public List<GroupMember> getGroupMembers(Long savingGoalId, String username) {
        List<GroupMember> memberList= new ArrayList<>();
        Group group = groupRepository.findBySavingGoalId(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        System.out.println("group: " + group);

        if (group!=null) {
            memberList = group.getMembers();
        }
        return memberList;
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