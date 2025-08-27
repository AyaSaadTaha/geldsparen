package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.MonthlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.status = 'PENDING' AND mp.dueDate < :currentDate")
    List<MonthlyPayment> findOverduePayments(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.status = 'PENDING' AND mp.dueDate = :dueDate")
    List<MonthlyPayment> findPaymentsDueTomorrow(@Param("dueDate") LocalDate dueDate);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.user.id = :userId AND mp.status = 'PENDING'")
    List<MonthlyPayment> findPendingPaymentsByUserId(@Param("userId") Long userId);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.user.id = :userId AND mp.status = 'PAID'")
    List<MonthlyPayment> findPaidPaymentsByUserId(@Param("userId") Long userId);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.user.id = :userId AND mp.status = 'OVERDUE'")
    List<MonthlyPayment> findOverduePaymentsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(mp) FROM MonthlyPayment mp WHERE mp.user.id = :userId AND mp.status = 'PENDING'")
    Long countPendingPaymentsByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(mp.amount) FROM MonthlyPayment mp WHERE mp.user.id = :userId AND mp.status = 'PENDING'")
    BigDecimal sumPendingAmountByUserId(@Param("userId") Long userId);
}