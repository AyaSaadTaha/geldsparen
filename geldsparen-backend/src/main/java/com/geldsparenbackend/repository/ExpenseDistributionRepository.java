package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.ExpenseDistribution;
import com.geldsparenbackend.model.EventExpense;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.model.ExpenseDistribution.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseDistributionRepository extends JpaRepository<ExpenseDistribution, Long> {
    List<ExpenseDistribution> findByExpense(EventExpense expense);
    List<ExpenseDistribution> findByUser(User user);
    List<ExpenseDistribution> findByStatus(PaymentStatus status);

    @Query("SELECT ed FROM ExpenseDistribution ed WHERE ed.user = :user AND ed.status = 'UNPAID'")
    List<ExpenseDistribution> findUnpaidDistributionsByUser(@Param("user") User user);

    @Query("SELECT SUM(ed.share) FROM ExpenseDistribution ed WHERE ed.user = :user AND ed.status = 'PAID'")
    BigDecimal getTotalPaidAmountByUser(@Param("user") User user);

    @Query("SELECT SUM(ed.share) FROM ExpenseDistribution ed WHERE ed.expense = :expense")
    BigDecimal getTotalDistributionByExpense(@Param("expense") EventExpense expense);
}