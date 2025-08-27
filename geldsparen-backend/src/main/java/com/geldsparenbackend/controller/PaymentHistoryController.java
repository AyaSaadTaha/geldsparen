package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.PaymentHistory;
import com.geldsparenbackend.service.PaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-history")
@CrossOrigin(origins = "*")
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    @Autowired
    public PaymentHistoryController(PaymentHistoryService paymentHistoryService) {
        this.paymentHistoryService = paymentHistoryService;
    }

    @GetMapping
    public List<PaymentHistory> getUserPaymentHistory(@AuthenticationPrincipal Long userId) {
        return paymentHistoryService.getUserPaymentHistory(userId);
    }

    @GetMapping("/saving-goal/{savingGoalId}")
    public List<PaymentHistory> getSavingGoalPaymentHistory(@PathVariable Long savingGoalId,
                                                            @AuthenticationPrincipal Long userId) {
        return paymentHistoryService.getPaymentHistoryForSavingGoal(savingGoalId);
    }

    @GetMapping("/recent")
    public List<PaymentHistory> getRecentPaymentHistory(@RequestParam(defaultValue = "10") int limit,
                                                        @AuthenticationPrincipal Long userId) {
        return paymentHistoryService.getRecentPaymentHistory(userId, limit);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getPaymentStats(@AuthenticationPrincipal Long userId) {
        try {
            double totalPaid = paymentHistoryService.getTotalPaidAmount(userId);
            int successfulPayments = paymentHistoryService.getSuccessfulPaymentsCount(userId);
            int overduePayments = paymentHistoryService.getOverduePaymentsCount(userId);

            return ResponseEntity.ok(new PaymentStatsResponse(totalPaid, successfulPayments, overduePayments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving payment statistics");
        }
    }

    // كلاس مساعد للرد بالإحصائيات
    public static class PaymentStatsResponse {
        public final double totalPaid;
        public final int successfulPayments;
        public final int overduePayments;

        public PaymentStatsResponse(double totalPaid, int successfulPayments, int overduePayments) {
            this.totalPaid = totalPaid;
            this.successfulPayments = successfulPayments;
            this.overduePayments = overduePayments;
        }
    }
}