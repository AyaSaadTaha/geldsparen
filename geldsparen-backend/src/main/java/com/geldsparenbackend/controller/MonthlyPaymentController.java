package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.MonthlyPayment;
import com.geldsparenbackend.model.MonthlyPayment.PaymentStatus;
import com.geldsparenbackend.service.MonthlyPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/monthly-payments")
public class MonthlyPaymentController {

    @Autowired
    private MonthlyPaymentService monthlyPaymentService;

    @GetMapping("/saving-goal/{savingGoalId}")
    public ResponseEntity<List<MonthlyPayment>> getMonthlyPaymentsBySavingGoal(
            @PathVariable Long savingGoalId, Authentication authentication) {
        String username = authentication.getName();
        List<MonthlyPayment> payments = monthlyPaymentService.getMonthlyPaymentsBySavingGoalId(savingGoalId, username);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/saving-goal/{savingGoalId}")
    public ResponseEntity<MonthlyPayment> createMonthlyPayment(@RequestBody MonthlyPayment monthlyPayment,
            @PathVariable Long savingGoalId,
            Authentication authentication) {
        String username = authentication.getName();
        MonthlyPayment createdPayment = monthlyPaymentService.createMonthlyPayment(monthlyPayment, savingGoalId, username);
        return ResponseEntity.ok(createdPayment);
    }

    @PatchMapping("/{paymentId}/status")
    public ResponseEntity<MonthlyPayment> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status,
            Authentication authentication) {
        String username = authentication.getName();
        MonthlyPayment updatedPayment = monthlyPaymentService.updateMonthlyPaymentStatus(paymentId, status, username);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<?> deleteMonthlyPayment(
            @PathVariable Long paymentId,
            Authentication authentication) {
        String username = authentication.getName();
        monthlyPaymentService.deleteMonthlyPayment(paymentId, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<MonthlyPayment>> getOverduePayments(Authentication authentication) {
        String username = authentication.getName();
        List<MonthlyPayment> overduePayments = monthlyPaymentService.getOverduePayments(username);
        return ResponseEntity.ok(overduePayments);
    }

    @GetMapping("/saving-goal/{savingGoalId}/pending-count")
    public ResponseEntity<Long> countPendingPayments(
            @PathVariable Long savingGoalId,
            Authentication authentication) {
        String username = authentication.getName();
        Long pendingCount = monthlyPaymentService.countPendingPaymentsBySavingGoalId(savingGoalId, username);
        return ResponseEntity.ok(pendingCount);
    }

    @GetMapping("/totalVerbleibendalleine/{savingGoalId}")
    public ResponseEntity<BigDecimal> getTotalVerbleibendalleine(@PathVariable Long savingGoalId, Authentication authentication) {
        String username = authentication.getName();
        BigDecimal total = monthlyPaymentService.getTotalVerbleibendalleine(savingGoalId, username);
        return ResponseEntity.ok(total);
    }
}