package com.geldsparenbackend.service;
import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.GroupMemberRepository;
import com.geldsparenbackend.repository.GroupRepository;
import com.geldsparenbackend.repository.MonthlyPaymentRepository;
import com.geldsparenbackend.repository.SavingGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private MonthlyPaymentRepository monthlyPaymentRepository;

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
}