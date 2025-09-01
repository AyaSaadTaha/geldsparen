package com.geldsparenbackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import jakarta.persistence.*;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@Entity
@Table(name = "saving_goals")
@ToString(exclude = "user") // Exclude the user relationship
public class SavingGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private String name;

    private BigDecimal targetAmount;

    private BigDecimal currentAmount = BigDecimal.ZERO;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private SavingGoalType type;

    @Enumerated(EnumType.STRING)
    private SavingGoalStatus status = SavingGoalStatus.ACTIVE;

    private BigDecimal monthlyAmount;
    private BigDecimal total_monthly_amount;

    public enum SavingGoalType {
        TRIP, BIRTHDAY, WEDDING, OTHER
    }

    public enum SavingGoalStatus {
        ACTIVE, COMPLETED, CANCELLED
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public SavingGoalType getType() { return type; }
    public void setType(SavingGoalType type) { this.type = type; }
    public SavingGoalStatus getStatus() { return status; }
    public void setStatus(SavingGoalStatus status) { this.status = status; }
    public BigDecimal getMonthlyAmount() { return monthlyAmount; }
    public void setMonthlyAmount(BigDecimal monthlyAmount) { this.monthlyAmount = monthlyAmount; }

    public BigDecimal getTotal_monthly_amount() {
        return total_monthly_amount;
    }

    public void setTotal_monthly_amount(BigDecimal total_monthly_amount) {
        this.total_monthly_amount = total_monthly_amount;
    }
/*
    public void calculateMonthlyAmount() {
        if (targetAmount != null && deadline != null) {
            long monthsBetween = ChronoUnit.MONTHS.between(LocalDate.now(), deadline);
            if (monthsBetween > 0) {
                monthlyAmount = targetAmount.divide(BigDecimal.valueOf(monthsBetween), 2, BigDecimal.ROUND_HALF_UP);
            } else {
                monthlyAmount = targetAmount;
            }
        }
    }
*/

    public int getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return currentAmount.divide(targetAmount, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }
}


