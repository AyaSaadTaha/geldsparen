package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.EventExpense;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EventExpenseRepository extends JpaRepository<EventExpense, Long> {
    List<EventExpense> findBySavingGoal(SavingGoal savingGoal);
    List<EventExpense> findByPaidBy(User user);

    @Query("SELECT ee FROM EventExpense ee WHERE ee.amount > :amount")
    List<EventExpense> findByAmountGreaterThan(@Param("amount") BigDecimal amount);

    @Query("SELECT SUM(ee.amount) FROM EventExpense ee WHERE ee.savingGoal = :savingGoal")
    BigDecimal getTotalExpensesBySavingGoal(@Param("savingGoal") SavingGoal savingGoal);
}