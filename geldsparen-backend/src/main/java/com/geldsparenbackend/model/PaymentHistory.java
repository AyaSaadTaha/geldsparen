package com.geldsparenbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_history")
public class PaymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "monthly_payment_id", nullable = false)
    private MonthlyPayment monthlyPayment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 20)
    private MonthlyPayment.PaymentStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 20)
    private MonthlyPayment.PaymentStatus newStatus;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "change_reason", length = 255)
    private String changeReason;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private User changedBy;

    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }

    // Arten von Veränderung Ursachen
    public enum ChangeReason {
        PAYMENT_MADE("Zahlung erfolgt"),
        PAYMENT_OVERDUE("Zahlung verspätet"),
        PAYMENT_RESCHEDULED("Zahlung verschoben"),
        PAYMENT_CANCELLED("Zahlung storniert"),
        PAYMENT_ADJUSTED(" Zahlungsbetrag anpassen"),
        SYSTEM_AUTOMATION("Automatische Änderung vom System"),
        MANUAL_UPDATE("Manuelle Anpassung");

        private final String description;

        ChangeReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // طريقة لإنشاء سجل تاريخ
    public static PaymentHistory createHistory(MonthlyPayment payment,
                                               MonthlyPayment.PaymentStatus oldStatus,
                                               MonthlyPayment.PaymentStatus newStatus,
                                               String reason,
                                               User changedBy) {
        PaymentHistory history = new PaymentHistory();
        history.setMonthlyPayment(payment);
        history.setUser(payment.getUser());
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setAmount(payment.getAmount());
        history.setDueDate(payment.getDueDate());
        history.setChangeReason(reason);
        history.setChangedBy(changedBy);
        return history;
    }
}