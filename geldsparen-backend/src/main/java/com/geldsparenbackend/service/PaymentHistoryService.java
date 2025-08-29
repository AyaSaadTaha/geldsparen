package com.geldsparenbackend.service;

import com.geldsparenbackend.model.MonthlyPayment;
import com.geldsparenbackend.model.PaymentHistory;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.MonthlyPaymentRepository;
import com.geldsparenbackend.repository.PaymentHistoryRepository;
import com.geldsparenbackend.repository.SavingGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PaymentHistoryService {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private MonthlyPaymentRepository monthlyPaymentRepository;

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private UserService userService;

    public List<PaymentHistory> getPaymentHistoryBySavingGoalId(Long savingGoalId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SavingGoal savingGoal = savingGoalRepository.findById(savingGoalId)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        if (!savingGoal.getUser().getId().equals(user.getId()) && !isGroupMember(savingGoalId, user.getId())) {
            throw new RuntimeException("Not authorized to view payment history");
        }

        return paymentHistoryRepository.findBySavingGoalId(savingGoalId);
    }

    public List<PaymentHistory> getPaymentHistoryByPaymentId(Long paymentId, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        MonthlyPayment payment = monthlyPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to view payment history");
        }

        return paymentHistoryRepository.findByMonthlyPaymentId(paymentId);
    }

    private boolean isGroupMember(Long savingGoalId, Long userId) {
        // Implement group member check logic
        return false;
    }
}