package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.PaymentHistory;
import com.geldsparenbackend.model.MonthlyPayment;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.monthlyPayment.savingGoal.id = :savingGoalId")
    List<PaymentHistory> findBySavingGoalId(@Param("savingGoalId") Long savingGoalId);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.monthlyPayment.id = :paymentId")
    List<PaymentHistory> findByMonthlyPaymentId(@Param("paymentId") Long paymentId);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.user.id = :userId")
    List<PaymentHistory> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.changedBy.id = :userId")
    List<PaymentHistory> findByChangedByUserId(@Param("userId") Long userId);
}