package com.geldsparenbackend.model;
import jakarta.persistence.*;
import lombok.Data;
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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SavingGoalType {
        TRIP, BIRTHDAY, WEDDING, OTHER
    }

    public enum SavingGoalStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}