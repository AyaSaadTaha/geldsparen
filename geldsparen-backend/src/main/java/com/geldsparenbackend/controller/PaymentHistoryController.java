package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.PaymentHistory;
import com.geldsparenbackend.service.PaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;



import java.util.List;

// PaymentHistoryController.java
@RestController
@RequestMapping("/api/payment-history")
public class PaymentHistoryController {

    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @GetMapping("/monthly-payment/{paymentId}")
    public ResponseEntity<List<PaymentHistory>> getPaymentHistoryByPaymentId(
            @PathVariable Long paymentId, Authentication authentication) {
        String username = authentication.getName();
        List<PaymentHistory> history = paymentHistoryService.getPaymentHistoryByPaymentId(paymentId, username);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/saving-goal/{savingGoalId}")
    public ResponseEntity<List<PaymentHistory>> getPaymentHistoryBySavingGoalId(
            @PathVariable Long savingGoalId, Authentication authentication) {
        String username = authentication.getName();
        List<PaymentHistory> history = paymentHistoryService.getPaymentHistoryBySavingGoalId(savingGoalId, username);
        return ResponseEntity.ok(history);
    }
}