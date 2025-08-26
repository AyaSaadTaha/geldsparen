package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.SavingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {
    List<SavingGoal> findByUserId(Long userId);

    @Query("SELECT sg FROM SavingGoal sg WHERE sg.user.id = :userId AND sg.status = 'ACTIVE'")
    List<SavingGoal> findActiveGoalsByUserId(@Param("userId") Long userId);

    @Query("SELECT sg FROM SavingGoal sg WHERE sg.deadline BETWEEN :startDate AND :endDate")
    List<SavingGoal> findGoalsByDeadlineRange(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}