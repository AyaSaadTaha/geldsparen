package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.EventExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventExpenseRepository extends JpaRepository<EventExpense, Long> {
    List<EventExpense> findBySavingGoalId(Long savingGoalId);

    @Query("SELECT ee FROM EventExpense ee WHERE ee.savingGoal.id = :savingGoalId ORDER BY ee.createdAt DESC")
    List<EventExpense> findRecentExpenses(@Param("savingGoalId") Long savingGoalId,
                                          org.springframework.data.domain.Pageable pageable);

    @Query("SELECT SUM(ee.amount) FROM EventExpense ee WHERE ee.savingGoal.id = :savingGoalId")
    Double getTotalExpensesBySavingGoalId(@Param("savingGoalId") Long savingGoalId);
}