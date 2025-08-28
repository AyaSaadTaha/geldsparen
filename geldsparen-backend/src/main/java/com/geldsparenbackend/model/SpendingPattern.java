package com.geldsparenbackend.model;
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
    private User user;

    private BigDecimal food;

    private BigDecimal clothes;

    private BigDecimal miscellaneous;

    private BigDecimal savings;

    // Getters and setters
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
}