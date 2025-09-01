package com.geldsparenbackend.service;
import com.geldsparenbackend.controller.GroupMemberInvitationDTO;
import com.geldsparenbackend.model.*;
import com.geldsparenbackend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupMemberService {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MonthlyPaymentRepository monthlyPaymentRepository;

    @Autowired
    private SpendingPatternRepository spendingPatternRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;



    public List<GroupMember> getGroupMembersBySavingGoalId(Long savingGoalId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Find the group associated with this saving goal
        Group group = groupRepository.findBySavingGoalId(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Group not found for this saving goal"));

        // Check authorization
        if (!savingGoal.getUser().getId().equals(user.getId()) &&
                !isGroupMember(group.getId(), user.getId())) {
            throw new RuntimeException("Not authorized to view group members");
        }

        return groupMemberRepository.findByGroupId(group.getId());
    }

    public Map<String, BigDecimal> getMemberContributions(Long savingGoalId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Find the group associated with this saving goal
        Group group = groupRepository.findBySavingGoalId(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Group not found for this saving goal"));

        // Check authorization
        if (!savingGoal.getUser().getId().equals(user.getId()) &&
                !isGroupMember(group.getId(), user.getId())) {
            throw new RuntimeException("Not authorized to view contributions");
        }

        Map<String, BigDecimal> contributions = new HashMap<>();
        List<GroupMember> members = groupMemberRepository.findByGroupId(group.getId());

        for (GroupMember member : members) {
            if (member.getInvitationStatus() == GroupMember.InvitationStatus.ACCEPTED) {
                BigDecimal totalContribution = monthlyPaymentRepository
                        .sumPaidAmountBySavingGoalIdAndUserId(savingGoalId, member.getUser().getId());
                contributions.put(member.getUser().getUsername(),
                        totalContribution != null ? totalContribution : BigDecimal.ZERO);
            }
        }

        return contributions;
    }

    private boolean isGroupMember(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .map(member -> member.getInvitationStatus() == GroupMember.InvitationStatus.ACCEPTED)
                .orElse(false);
    }



    public List<GroupMemberInvitationDTO> getPendingInvitations(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Get invitations for registered user
        List<GroupMember> userInvitations = groupMemberRepository.findByUserAndInvitationStatus(user, GroupMember.InvitationStatus.PENDING);

        // Also get invitations sent to user's email (in case they were invited before registering)
        List<GroupMember> emailInvitations = groupMemberRepository.findByEmailAndInvitationStatus(user.getEmail(), GroupMember.InvitationStatus.PENDING);

        // Combine both lists
        List<GroupMember> allInvitations = new ArrayList<>();
        allInvitations.addAll(userInvitations);
        allInvitations.addAll(emailInvitations);

        return allInvitations.stream().map(invitation -> {
            GroupMemberInvitationDTO dto = new GroupMemberInvitationDTO();
            dto.setId(invitation.getId());
            dto.setGroupName(invitation.getGroup().getName());
            dto.setSavingGoalName(invitation.getGroup().getSavingGoal().getName());
            //dto.setInvitedBy(invitation.getGroup().getCreatedBy().getUsername());
            dto.setInvitedBy(invitation.getGroup().getSavingGoal().getUser().getUsername());

            // Calculate monthly contribution
            SavingGoal goal = invitation.getGroup().getSavingGoal();
            long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now(), goal.getDeadline());
            if (monthsBetween <= 0) monthsBetween = 1; // Prevent division by zero

            BigDecimal monthlyAmount = goal.getTargetAmount().divide(BigDecimal.valueOf(monthsBetween), 2, RoundingMode.HALF_UP);

            // Count accepted members
            long acceptedMembers = invitation.getGroup().getMembers().stream()
                    .filter(m -> m.getInvitationStatus() == GroupMember.InvitationStatus.ACCEPTED)
                    .count();

            // Divide by number of accepted members + 1 (for the user if they accept)
            BigDecimal userMonthlyContribution = monthlyAmount.divide(
                    BigDecimal.valueOf(acceptedMembers + 1), 2, RoundingMode.HALF_UP);

            dto.setMonthlyContribution(userMonthlyContribution);

            return dto;
        }).collect(Collectors.toList());
    }

    public Long countPendingInvitations(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Count user-based invitations
        Long userCount = groupMemberRepository.countByUserAndInvitationStatus(user, GroupMember.InvitationStatus.PENDING);

        // Count email-based invitations
        List<GroupMember> emailInvitations = groupMemberRepository.findByEmailAndInvitationStatus(user.getEmail(), GroupMember.InvitationStatus.PENDING);

        return userCount + emailInvitations.size();
    }

    @Transactional
    public void respondToGroupInvitation(Long groupMemberId, GroupMember.InvitationStatus response, String username) {
        User user = userService.findByUsername(username);
        GroupMember groupMember = groupMemberRepository.findById(groupMemberId)
                .orElseThrow(() -> new RuntimeException("Group invitation not found"));

        // Check if the invitation is for this user (by user ID or email)
        boolean isForUser = (groupMember.getUser() != null && groupMember.getUser().getId().equals(user.getId())) ||
                (groupMember.getEmail() != null && groupMember.getEmail().equals(user.getEmail()));

        if (!isForUser) {
            throw new RuntimeException("This invitation is not for you");
        }

        // If user was invited by email only, now associate the user object
        if (groupMember.getUser() == null) {
            groupMember.setUser(user);
        }

        groupMember.setInvitationStatus(response);

        if (response == GroupMember.InvitationStatus.ACCEPTED) {
            groupMember.setJoinedAt(LocalDateTime.now());

            // Check if user can afford the monthly contribution
            checkAffordability(groupMember, user);
        }

        groupMemberRepository.save(groupMember);

        // Send notification about the response
        notificationService.sendGroupInvitationResponse(groupMember, response);
    }

    private void checkAffordability(GroupMember groupMember, User user) {
        SavingGoal goal = groupMember.getGroup().getSavingGoal();

        // Calculate monthly amount
        long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now(), goal.getDeadline());
        if (monthsBetween <= 0) monthsBetween = 1;

        BigDecimal monthlyAmount = goal.getTargetAmount().divide(BigDecimal.valueOf(monthsBetween), 2, RoundingMode.HALF_UP);

        // Count accepted members
        long acceptedMembers = groupMember.getGroup().getMembers().stream()
                .filter(m -> m.getInvitationStatus() == GroupMember.InvitationStatus.ACCEPTED)
                .count();

        // User's monthly contribution
        BigDecimal userMonthlyContribution = monthlyAmount.divide(
                BigDecimal.valueOf(acceptedMembers), 2, RoundingMode.HALF_UP);

        // Check if user can afford this
        Optional<SpendingPattern> spendingPattern = spendingPatternRepository.findByUser(user);
        Optional<CurrentAccount> currentAccount = currentAccountRepository.findByUser(user);

        if (spendingPattern.isPresent() && currentAccount.isPresent()) {
            BigDecimal availableSavings = spendingPattern.get().getSavings();

            if (availableSavings.compareTo(userMonthlyContribution) < 0) {
                throw new RuntimeException("Your available savings (€" + availableSavings +
                        ") are not enough for your monthly contribution of €" + userMonthlyContribution +
                        ". You need to reduce your expenses or increase your income.");
            }
        }
    }

}