package com.geldsparenbackend.model;

import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "saving_goals")
public class SavingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal monthlyAmount;

    @Column(nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingGoalType type;

    @Enumerated(EnumType.STRING)
    private SavingGoalStatus status = SavingGoalStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "savingGoal", cascade = CascadeType.ALL)
    private Group group;

    @OneToMany(mappedBy = "savingGoal", cascade = CascadeType.ALL)
    private List<MonthlyPayment> monthlyPayments = new ArrayList<>();

    @OneToMany(mappedBy = "savingGoal", cascade = CascadeType.ALL)
    private List<EventExpense> eventExpenses = new ArrayList<>();

    @OneToMany(mappedBy = "savingGoal", cascade = CascadeType.ALL)
    private List<EventPhoto> eventPhotos = new ArrayList<>();

    @OneToMany(mappedBy = "savingGoal", cascade = CascadeType.ALL)
    private List<EventReview> eventReviews = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateMonthlyAmount(); // حساب المبلغ الشهري تلقائياً
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateMonthlyAmount(); // إعادة الحساب عند التحديث
    }

    /**
     * حساب المبلغ الشهري المطلوب
     */
    public void calculateMonthlyAmount() {
        if (targetAmount != null && deadline != null) {
            long monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(
                    LocalDate.now().withDayOfMonth(1),
                    deadline.withDayOfMonth(1)
            );

            if (monthsBetween > 0) {
                this.monthlyAmount = targetAmount
                        .divide(BigDecimal.valueOf(monthsBetween), 2, BigDecimal.ROUND_HALF_UP);
            } else {
                this.monthlyAmount = targetAmount;
            }
        }
    }

    /**
     * الحصول على عدد الأشهر المتبقية
     */
    public long getRemainingMonths() {
        if (deadline == null) return 0;

        return java.time.temporal.ChronoUnit.MONTHS.between(
                LocalDate.now().withDayOfMonth(1),
                deadline.withDayOfMonth(1)
        );
    }

    /**
     * التحقق إذا كان الهدف قد تحقق
     */
    public boolean isGoalAchieved() {
        return currentAmount != null &&
                targetAmount != null &&
                currentAmount.compareTo(targetAmount) >= 0;
    }

    /**
     * الحصول على نسبة الإنجاز
     */
    public BigDecimal getProgressPercentage() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (currentAmount == null) {
            return BigDecimal.ZERO;
        }

        return currentAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, BigDecimal.ROUND_HALF_UP);
    }

    public enum SavingGoalType {
        TRIP, BIRTHDAY, WEDDING, OTHER
    }

    public enum SavingGoalStatus {
        ACTIVE, COMPLETED, CANCELLED
    }

    // Getters and Setters (يتم إنشاؤها تلقائياً بواسطة @Data من Lombok)
    // ولكن نضيف setter manually إذا كان Lombok لا يعمل بشكل صحيح

    public void setMonthlyAmount(BigDecimal monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }
}