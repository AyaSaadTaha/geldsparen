package com.geldsparenbackend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "current_accounts")
public class CurrentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private BigDecimal salary;

    private Integer payday; // Day of month (1-31)

    private String iban;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public Integer getPayday() { return payday; }
    public void setPayday(Integer payday) { this.payday = payday; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
}