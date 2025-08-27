package com.geldsparenbackend.service;

import com.geldsparenbackend.model.MonthlyPayment;
import com.geldsparenbackend.model.PaymentHistory;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.PaymentHistoryRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentHistoryService {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository,
                                 UserRepository userRepository) {
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.userRepository = userRepository;
    }

    public List<PaymentHistory> getUserPaymentHistory(Long userId) {
        return paymentHistoryRepository.findByUserId(userId);
    }

    public List<PaymentHistory> getPaymentHistoryForSavingGoal(Long savingGoalId) {
        return paymentHistoryRepository.findBySavingGoalId(savingGoalId);
    }

    public PaymentHistory recordPaymentStatusChange(MonthlyPayment payment,
                                                    MonthlyPayment.PaymentStatus oldStatus,
                                                    MonthlyPayment.PaymentStatus newStatus,
                                                    String reason,
                                                    Long changedByUserId) {
        User changedBy = userRepository.findById(changedByUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + changedByUserId));

        PaymentHistory history = PaymentHistory.createHistory(
                payment, oldStatus, newStatus, reason, changedBy
        );

        return paymentHistoryRepository.save(history);
    }

    public PaymentHistory recordPayment(MonthlyPayment payment,
                                        MonthlyPayment.PaymentStatus newStatus,
                                        String reason,
                                        Long changedByUserId) {
        return recordPaymentStatusChange(payment, payment.getStatus(), newStatus, reason, changedByUserId);
    }

    public List<PaymentHistory> getRecentPaymentHistory(Long userId, int limit) {
        List<PaymentHistory> allHistory = paymentHistoryRepository.findRecentByUserId(userId);
        return allHistory.stream()
                .limit(limit)
                .toList();
    }

    public double getTotalPaidAmount(Long userId) {
        return paymentHistoryRepository.findPaidPaymentsByUserId(userId).stream()
                .mapToDouble(history -> history.getAmount().doubleValue())
                .sum();
    }

    public int getSuccessfulPaymentsCount(Long userId) {
        return paymentHistoryRepository.findPaidPaymentsByUserId(userId).size();
    }

    public int getOverduePaymentsCount(Long userId) {
        List<PaymentHistory> userHistory = paymentHistoryRepository.findByUserId(userId);
        return (int) userHistory.stream()
                .filter(history -> history.getNewStatus() == MonthlyPayment.PaymentStatus.OVERDUE)
                .count();
    }
}