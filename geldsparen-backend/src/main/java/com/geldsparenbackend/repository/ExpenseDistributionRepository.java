package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.ExpenseDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseDistributionRepository extends JpaRepository<ExpenseDistribution, Long> {
    List<ExpenseDistribution> findByExpenseId(Long expenseId);

    List<ExpenseDistribution> findByUserId(Long userId);

    @Query("SELECT ed FROM ExpenseDistribution ed WHERE ed.user.id = :userId AND ed.status = 'UNPAID'")
    List<ExpenseDistribution> findUnpaidDistributionsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(ed.share) FROM ExpenseDistribution ed WHERE ed.user.id = :userId AND ed.status = 'UNPAID'")
    Double getTotalUnpaidAmountByUserId(@Param("userId") Long userId);
}