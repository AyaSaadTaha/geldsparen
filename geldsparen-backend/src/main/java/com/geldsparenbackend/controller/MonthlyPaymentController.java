package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.MonthlyPayment;
import com.geldsparenbackend.service.MonthlyPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/monthly-payments")
@CrossOrigin(origins = "*")
public class MonthlyPaymentController {
    private final MonthlyPaymentService monthlyPaymentService;

    @Autowired
    public MonthlyPaymentController(MonthlyPaymentService monthlyPaymentService) {
        this.monthlyPaymentService = monthlyPaymentService;
    }

    @GetMapping
    public List<MonthlyPayment> getUserPayments(@AuthenticationPrincipal Long userId) {
        return monthlyPaymentService.getPaymentsByUserId(userId);
    }

    @GetMapping("/saving-goal/{savingGoalId}")
    public List<MonthlyPayment> getSavingGoalPayments(@PathVariable Long savingGoalId,
                                                      @AuthenticationPrincipal Long userId) {
        return monthlyPaymentService.getPaymentsBySavingGoalId(savingGoalId);
    }

    @GetMapping("/due-range")
    public List<MonthlyPayment> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal Long userId) {
        return monthlyPaymentService.getPaymentsByDateRange(userId, startDate, endDate);
    }

    @PostMapping("/{paymentId}/pay")
    public ResponseEntity<MonthlyPayment> markPaymentAsPaid(@PathVariable Long paymentId,
                                                            @AuthenticationPrincipal Long userId) {
        try {
            MonthlyPayment payment = monthlyPaymentService.markPaymentAsPaid(paymentId, userId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{paymentId}/reschedule")
    public ResponseEntity<MonthlyPayment> reschedulePayment(
            @PathVariable Long paymentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDueDate,
            @AuthenticationPrincipal Long userId) {
        try {
            MonthlyPayment payment = monthlyPaymentService.reschedulePayment(paymentId, newDueDate, userId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{paymentId}/amount")
    public ResponseEntity<MonthlyPayment> updatePaymentAmount(
            @PathVariable Long paymentId,
            @RequestParam BigDecimal newAmount,
            @AuthenticationPrincipal Long userId) {
        try {
            MonthlyPayment payment = monthlyPaymentService.updatePaymentAmount(paymentId, newAmount, userId);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/overdue")
    public List<MonthlyPayment> getOverduePayments(@AuthenticationPrincipal Long userId) {
        return monthlyPaymentService.getOverduePayments().stream()
                .filter(payment -> payment.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @GetMapping("/due-tomorrow")
    public List<MonthlyPayment> getPaymentsDueTomorrow(@AuthenticationPrincipal Long userId) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return monthlyPaymentService.getPaymentsDueTomorrow(tomorrow).stream()
                .filter(payment -> payment.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @GetMapping("/total-due")
    public BigDecimal getTotalDueAmount(@AuthenticationPrincipal Long userId) {
        return monthlyPaymentService.getTotalDueAmount(userId);
    }

    @GetMapping("/stats")
    public ResponseEntity<MonthlyPaymentService.PaymentStatistics> getPaymentStatistics(
            @AuthenticationPrincipal Long userId) {
        try {
            MonthlyPaymentService.PaymentStatistics stats = monthlyPaymentService.getPaymentStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/saving-goal/{savingGoalId}")
    public ResponseEntity<Void> deleteSavingGoalPayments(@PathVariable Long savingGoalId,
                                                         @AuthenticationPrincipal Long userId) {
        try {
            monthlyPaymentService.deletePaymentsBySavingGoalId(savingGoalId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}