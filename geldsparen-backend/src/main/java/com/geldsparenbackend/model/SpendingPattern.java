package com.geldsparenbackend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "spending_patterns")
public class SpendingPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private BigDecimal food;

    private BigDecimal clothes;

    private BigDecimal renter;

    private BigDecimal miscellaneous;

    private BigDecimal savings;

    private BigDecimal total_expenses;
    private BigDecimal total_income;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and setters

    public BigDecimal getTotal_expenses() {return total_expenses;}

    public void setTotal_expenses(BigDecimal total_expenses) {this.total_expenses = total_expenses;}

    public BigDecimal getTotal_income() {return total_income;}

    public void setTotal_income(BigDecimal total_income) {this.total_income = total_income;}

    public LocalDateTime getCreatedAt() {return createdAt;}

    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public LocalDateTime getUpdatedAt() {return updatedAt;}

    public void setUpdatedAt(LocalDateTime updatedAt) {this.updatedAt = updatedAt;}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public BigDecimal getFood() { return food; }
    public void setFood(BigDecimal food) { this.food = food; }
    public BigDecimal getClothes() { return clothes; }
    public void setClothes(BigDecimal clothes) { this.clothes = clothes; }
    public BigDecimal getMiscellaneous() { return miscellaneous; }
    public void setMiscellaneous(BigDecimal miscellaneous) { this.miscellaneous = miscellaneous; }
    public BigDecimal getSavings() { return savings; }
    public void setSavings(BigDecimal savings) { this.savings = savings; }

    public BigDecimal getRenter() {
        return renter;
    }

    public void setRenter(BigDecimal renter) {
        this.renter = renter;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}