package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.MonthlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, Long> {
    List<MonthlyPayment> findBySavingGoalId(Long savingGoalId);

    List<MonthlyPayment> findByUserId(Long userId);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.user.id = :userId AND mp.dueDate BETWEEN :startDate AND :endDate")
    List<MonthlyPayment> findByUserIdAndDueDateRange(@Param("userId") Long userId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.status = 'PENDING' AND mp.dueDate <= :date")
    List<MonthlyPayment> findOverduePayments(@Param("date") LocalDate date);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.status = 'PENDING' AND mp.dueDate = :tomorrow")
    List<MonthlyPayment> findPaymentsDueTomorrow(@Param("tomorrow") LocalDate tomorrow);
}