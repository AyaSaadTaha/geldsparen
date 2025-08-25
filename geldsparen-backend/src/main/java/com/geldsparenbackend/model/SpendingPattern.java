package com.geldsparenbackend.model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "spending_patterns")
public class SpendingPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 10, scale = 2)
    private BigDecimal food;

    @Column(precision = 10, scale = 2)
    private BigDecimal clothes;

    @Column(precision = 10, scale = 2)
    private BigDecimal miscellaneous;

    @Column(precision = 10, scale = 2)
    private BigDecimal savings;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalIncome;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalExpenses;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "spendingPattern", cascade = CascadeType.ALL)
    private List<SpendingPatternDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }

    private void calculateTotals() {
        this.totalExpenses = (food != null ? food : BigDecimal.ZERO)
                .add(clothes != null ? clothes : BigDecimal.ZERO)
                .add(miscellaneous != null ? miscellaneous : BigDecimal.ZERO);

        this.totalIncome = (totalExpenses != null ? totalExpenses : BigDecimal.ZERO)
                .add(savings != null ? savings : BigDecimal.ZERO);
    }

    public BigDecimal getRemainingAmount() {
        return totalIncome.subtract(totalExpenses).subtract(savings != null ? savings : BigDecimal.ZERO);
    }
}