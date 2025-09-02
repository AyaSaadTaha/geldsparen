package com.geldsparenbackend.service;

import com.geldsparenbackend.model.*;
import com.geldsparenbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SavingGoalService {

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private SpendingPatternRepository spendingPatternRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Autowired
    private UserService userService;

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

    public SavingGoal createSavingGoal(SavingGoal savingGoal, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Calculate monthly amount
        // Calculate months between including both start and end months
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = savingGoal.getDeadline();

        // More accurate month calculation that includes both start and end months
        long monthsBetween = calculateTotalMonths(startDate, endDate);

        BigDecimal monthlyAmount = savingGoal.getTargetAmount()
                .divide(BigDecimal.valueOf(monthsBetween), 2, RoundingMode.HALF_UP);

        savingGoal.setMonthlyAmount(monthlyAmount);

        savingGoal.setTotal_monthly_amount(
                monthlyAmount.multiply(BigDecimal.valueOf(monthsBetween))
        );
        savingGoal.setTotal_monthly_number(BigDecimal.valueOf(monthsBetween));

        // Check if user can afford this saving goal
        Optional<SpendingPattern> spendingPattern = spendingPatternRepository.findByUser(user);
        Optional<CurrentAccount> currentAccount = currentAccountRepository.findByUser(user);
        String warningMessage = null;

        if (spendingPattern.isPresent() && currentAccount.isPresent()) {
            BigDecimal availableSavings = spendingPattern.get().getSavings();

            if (availableSavings.compareTo(savingGoal.getMonthlyAmount()) < 0) {
                warningMessage = "Ihre verfügbaren Ersparnisse (€" + availableSavings +
                                ") reichen nicht für die monatliche Zahlung von €" + savingGoal.getMonthlyAmount() +
                                ". Sie müssen Ihre Ausgaben reduzieren oder Ihr Einkommen erhöhen.";
            }
        }
        savingGoal.setUser(user);
        return savingGoalRepository.save(savingGoal);
    }

    public List<Map<String, Object>> getUserSavingGoals(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<Map<String, Object>> result = new ArrayList<>();

        // الحصول على أهداف التوفير الخاصة بالمستخدم
        List<SavingGoal> personalGoals = savingGoalRepository.findByUser(user);

        // إضافة الأهداف الشخصية
        for (SavingGoal goal : personalGoals) {
            Map<String, Object> goalMap = convertToMap(goal);

            // التحقق إذا كان الهدف جزء من مجموعة
            Group group = groupRepository.findBySavingGoals(goal);
            if (group != null) {
                // هذا هدف جماعي أنشأه المستخدم
                goalMap.put("isPersonal", false);
                goalMap.put("isGroup", true);
                goalMap.put("isInvited", false);
                goalMap.put("groupName", group.getName());
            } else {
                // هذا هدف فردي
                goalMap.put("isPersonal", true);
                goalMap.put("isGroup", false);
                goalMap.put("isInvited", false);
            }

            result.add(goalMap);
        }

        // الحصول على أهداف التوفير التي تمت دعوة المستخدم لها ووافق عليها
        List<GroupMember> acceptedInvitations = groupMemberRepository.findByUserAndInvitationStatus(
                user, GroupMember.InvitationStatus.ACCEPTED);

        for (GroupMember invitation : acceptedInvitations) {
            SavingGoal goal = invitation.getGroup().getSavingGoal();

            // تجنب تكرار الأهداف إذا كانت موجودة بالفعل في القائمة
            boolean alreadyExists = result.stream()
                    .anyMatch(g -> g.get("id").equals(goal.getId()));

            if (!alreadyExists) {
                Map<String, Object> goalMap = convertToMap(goal);
                goalMap.put("isPersonal", false);
                goalMap.put("isGroup", true);
                goalMap.put("isInvited", true);

                // الحصول على اسم المجموعة
                Group group = groupRepository.findBySavingGoals(goal);
                if (group != null) {
                    goalMap.put("groupName", group.getName());
                }

                result.add(goalMap);
            }
        }

        return result;
    }

    private Map<String, Object> convertToMap(SavingGoal goal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", goal.getId());
        map.put("name", goal.getName());
        map.put("targetAmount", goal.getTargetAmount());
        map.put("currentAmount", goal.getCurrentAmount());
        map.put("deadline", goal.getDeadline());
        map.put("type", goal.getType());
        map.put("status", goal.getStatus());
        map.put("monthlyAmount", goal.getMonthlyAmount());
        return map;
    }

    public SavingGoal getSavingGoal(Long id, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Verify that the saving goal belongs to the authenticated user
      /*  if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to access this saving goal");
        }*/
        return savingGoal;
    }

    public SavingGoal updateSavingGoal(Long id, SavingGoal savingGoalDetails, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Verify that the saving goal belongs to the authenticated user
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this saving goal");
        }

        savingGoal.setName(savingGoalDetails.getName());
        savingGoal.setTargetAmount(savingGoalDetails.getTargetAmount());
        savingGoal.setDeadline(savingGoalDetails.getDeadline());
        savingGoal.setType(savingGoalDetails.getType());

        // Calculate months between including both start and end months
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = savingGoal.getDeadline();

        // More accurate month calculation that includes both start and end months
        long monthsBetween = calculateTotalMonths(startDate, endDate);

        BigDecimal monthlyAmount = savingGoal.getTargetAmount()
                .divide(BigDecimal.valueOf(monthsBetween), 2, RoundingMode.HALF_UP);

        savingGoal.setMonthlyAmount(monthlyAmount);

        return savingGoalRepository.save(savingGoal);
    }

    public void deleteSavingGoal(Long id, String username) {
        User user = userService.findByUsername(username);
        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Verify that the saving goal belongs to the authenticated user
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this saving goal");
        }

        savingGoalRepository.delete(savingGoal);
    }

}