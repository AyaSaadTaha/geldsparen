package com.geldsparenbackend.service;

import com.geldsparenbackend.model.*;
import com.geldsparenbackend.repository.CurrentAccountRepository;
import com.geldsparenbackend.repository.MonthlyPaymentRepository;
import com.geldsparenbackend.repository.SavingGoalRepository;
import com.geldsparenbackend.repository.SpendingPatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MonthlyPaymentService {

    @Autowired
    private MonthlyPaymentRepository monthlyPaymentRepository;

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private SpendingPatternRepository spendingPatternRepository;

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    public List<MonthlyPayment> getMonthlyPaymentsBySavingGoalId(Long savingGoalId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return monthlyPaymentRepository.findBySavingGoalIdAndUsers(savingGoalId,user);
    }

    public BigDecimal  getTotalVerbleibendalleine(Long savingGoalId, String username) {
        List<MonthlyPayment> monthlyPayments = getMonthlyPaymentsBySavingGoalId(savingGoalId, username);
        BigDecimal totalPaid = BigDecimal.ZERO;

        if (monthlyPayments != null && !monthlyPayments.isEmpty()) {
            totalPaid = monthlyPayments.stream()
                    .filter(m -> m.getStatus().equals(MonthlyPayment.PaymentStatus.PAID))
                    .map(MonthlyPayment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return totalPaid;
    }

    public MonthlyPayment createMonthlyPayment(MonthlyPayment monthlyPayment, Long savingGoalId, String username) {

        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        monthlyPayment.setSavingGoal(savingGoal);
        monthlyPayment.setUser(user);
        monthlyPayment.setStatus(MonthlyPayment.PaymentStatus.PENDING);

        return monthlyPaymentRepository.save(monthlyPayment);
    }

    public MonthlyPayment updateMonthlyPaymentStatus(Long paymentId, MonthlyPayment.PaymentStatus status, String username) {
        User user = userService.findByUsername(username);
        MonthlyPayment monthlyPayment = monthlyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Monthly payment not found"));

        if (!monthlyPayment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this payment");
        }

        monthlyPayment.setStatus(status);

        if (status == MonthlyPayment.PaymentStatus.PAID) {
            monthlyPayment.setPaidAt(LocalDate.now().atStartOfDay());

            // تحديث المبلغ الحالي في هدف التوفير
            SavingGoal savingGoal = monthlyPayment.getSavingGoal();
            savingGoal.setCurrentAmount(savingGoal.getCurrentAmount().add(monthlyPayment.getAmount()));
            savingGoalRepository.save(savingGoal);
        }

        return monthlyPaymentRepository.save(monthlyPayment);
    }

    public void deleteMonthlyPayment(Long paymentId, String username) {
        User user = userService.findByUsername(username);
        MonthlyPayment monthlyPayment = monthlyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Monthly payment not found"));

        if (!monthlyPayment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this payment");
        }

        monthlyPaymentRepository.delete(monthlyPayment);
    }

    public List<MonthlyPayment> getOverduePayments(String username) {
        User user = userService.findByUsername(username);
        return monthlyPaymentRepository.findOverduePayments(LocalDate.now());
    }

    public Long countPendingPaymentsBySavingGoalId(Long savingGoalId, String username) {
        User user = userService.findByUsername(username);
        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // التحقق من أن هدف التوفير ينتمي للمستخدم المصادق
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to access this information");
        }

        return monthlyPaymentRepository.countBySavingGoalIdAndStatus(savingGoalId, MonthlyPayment.PaymentStatus.PENDING);
    }
}