package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SavingGoalRepository extends JpaRepository<SavingGoal, Long> {
    List<SavingGoal> findByUser(User user);
    List<SavingGoal> findByUserAndStatus(User user, SavingGoal.SavingGoalStatus status);

    @Query("SELECT sg FROM SavingGoal sg WHERE sg.deadline <= :date AND sg.status = 'ACTIVE'")
    List<SavingGoal> findUpcomingDeadlines(@Param("date") LocalDate date);

    @Query("SELECT sg FROM SavingGoal sg WHERE sg.currentAmount >= sg.targetAmount AND sg.status = 'ACTIVE'")
    List<SavingGoal> findCompletedGoals();

    Long countByUser(User user);
}