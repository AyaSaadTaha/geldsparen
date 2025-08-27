package com.geldsparenbackend.service;

import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.SavingGoalRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class SavingGoalService {
    private final SavingGoalRepository savingGoalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final MonthlyPaymentService monthlyPaymentService;

    @Autowired
    public SavingGoalService(SavingGoalRepository savingGoalRepository,
                             UserRepository userRepository,
                             NotificationService notificationService,
                             MonthlyPaymentService monthlyPaymentService) {
        this.savingGoalRepository = savingGoalRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.monthlyPaymentService = monthlyPaymentService;
    }

    public List<SavingGoal> getUserSavingGoals(Long userId) {
        return savingGoalRepository.findByUserId(userId);
    }

    public Optional<SavingGoal> getSavingGoalById(Long id) {
        return savingGoalRepository.findById(id);
    }

    public SavingGoal createSavingGoal(SavingGoal savingGoal, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        savingGoal.setUser(user);

        // حساب المبلغ الشهري المطلوب
        long monthsBetween = ChronoUnit.MONTHS.between(
                LocalDate.now().withDayOfMonth(1),
                savingGoal.getDeadline().withDayOfMonth(1)
        );

        if (monthsBetween <= 0) {
            throw new RuntimeException("Deadline must be in the future");
        }

        BigDecimal monthlyAmount = savingGoal.getTargetAmount()
                .divide(BigDecimal.valueOf(monthsBetween), 2, BigDecimal.ROUND_HALF_UP);

        savingGoal.setMonthlyAmount(monthlyAmount);

        SavingGoal savedGoal = savingGoalRepository.save(savingGoal);

        // إنشاء الدفعات الشهرية تلقائياً
        monthlyPaymentService.createMonthlyPaymentsForSavingGoal(savedGoal);

        return savedGoal;
    }

    public SavingGoal updateSavingGoal(Long id, SavingGoal savingGoalDetails) {
        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with id: " + id));

        savingGoal.setName(savingGoalDetails.getName());
        savingGoal.setTargetAmount(savingGoalDetails.getTargetAmount());
        savingGoal.setDeadline(savingGoalDetails.getDeadline());
        savingGoal.setType(savingGoalDetails.getType());

        // إعادة حساب المبلغ الشهري
        long monthsBetween = ChronoUnit.MONTHS.between(
                LocalDate.now().withDayOfMonth(1),
                savingGoal.getDeadline().withDayOfMonth(1)
        );

        BigDecimal monthlyAmount = savingGoal.getTargetAmount()
                .divide(BigDecimal.valueOf(monthsBetween), 2, BigDecimal.ROUND_HALF_UP);

        savingGoal.setMonthlyAmount(monthlyAmount);

        // حذف الدفعات القديمة وإنشاء جديدة
        monthlyPaymentService.deletePaymentsBySavingGoalId(id);
        monthlyPaymentService.createMonthlyPaymentsForSavingGoal(savingGoal);

        return savingGoalRepository.save(savingGoal);
    }

    public void deleteSavingGoal(Long id) {
        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with id: " + id));

        // حذف الدفعات المرتبطة أولاً
        monthlyPaymentService.deletePaymentsBySavingGoalId(id);

        savingGoalRepository.delete(savingGoal);
    }

    public void addToCurrentAmount(Long savingGoalId, BigDecimal amount) {
        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found with id: " + savingGoalId));

        BigDecimal newAmount = savingGoal.getCurrentAmount().add(amount);
        savingGoal.setCurrentAmount(newAmount);

        // التحقق إذا تم تحقيق الهدف
        if (newAmount.compareTo(savingGoal.getTargetAmount()) >= 0) {
            savingGoal.setStatus(SavingGoal.SavingGoalStatus.COMPLETED);

            notificationService.createNotification(
                    savingGoal.getUser().getId(),
                    "تهانينا! 🎉",
                    String.format("لقد حققت هدفك التوفيري '%s' بنجاح!", savingGoal.getName()),
                    "goal_achieved"
            );
        }

        savingGoalRepository.save(savingGoal);
    }

    public BigDecimal calculateRecommendedMonthlySavings(Long userId, BigDecimal goalAmount, int months) {
        if (months <= 0) {
            throw new RuntimeException("Number of months must be greater than zero");
        }

        return goalAmount.divide(BigDecimal.valueOf(months), 2, BigDecimal.ROUND_HALF_UP);
    }

    public void checkAndNotifyUpcomingPayments() {
        // سيتم تنفيذ هذا في جزء لاحق
    }
}