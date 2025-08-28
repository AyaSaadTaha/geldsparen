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
    List<PaymentHistory> findByMonthlyPayment(MonthlyPayment monthlyPayment);
    List<PaymentHistory> findByUser(User user);
    List<PaymentHistory> findByChangedBy(User user);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.changedAt BETWEEN :startDate AND :endDate")
    List<PaymentHistory> findByChangeDateBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.changeReason = :reason")
    List<PaymentHistory> findByChangeReason(@Param("reason") String reason);
}