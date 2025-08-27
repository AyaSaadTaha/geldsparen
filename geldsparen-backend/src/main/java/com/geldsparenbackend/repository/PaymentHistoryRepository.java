package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByUserId(Long userId);

    List<PaymentHistory> findByMonthlyPaymentId(Long monthlyPaymentId);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.user.id = :userId ORDER BY ph.changedAt DESC")
    List<PaymentHistory> findRecentByUserId(@Param("userId") Long userId);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.monthlyPayment.savingGoal.id = :savingGoalId")
    List<PaymentHistory> findBySavingGoalId(@Param("savingGoalId") Long savingGoalId);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.changedAt BETWEEN :startDate AND :endDate")
    List<PaymentHistory> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.newStatus = 'PAID' AND ph.user.id = :userId")
    List<PaymentHistory> findPaidPaymentsByUserId(@Param("userId") Long userId);
}