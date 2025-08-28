package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.MonthlyPayment;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.model.MonthlyPayment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, Long> {
    List<MonthlyPayment> findBySavingGoal(SavingGoal savingGoal);

    // الطريقة الصحيحة للبحث بـ savingGoalId
    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.savingGoal.id = :savingGoalId")
    List<MonthlyPayment> findBySavingGoalId(@Param("savingGoalId") Long savingGoalId);

    List<MonthlyPayment> findByUser(User user);
    List<MonthlyPayment> findByStatus(PaymentStatus status);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.dueDate <= :date AND mp.status = 'PENDING'")
    List<MonthlyPayment> findOverduePayments(@Param("date") LocalDate date);

    @Query("SELECT mp FROM MonthlyPayment mp WHERE mp.dueDate BETWEEN :startDate AND :endDate")
    List<MonthlyPayment> findPaymentsBetweenDates(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    Long countBySavingGoalAndStatus(SavingGoal savingGoal, PaymentStatus status);

    // طريقة إضافية للعد بـ savingGoalId
    @Query("SELECT COUNT(mp) FROM MonthlyPayment mp WHERE mp.savingGoal.id = :savingGoalId AND mp.status = :status")
    Long countBySavingGoalIdAndStatus(@Param("savingGoalId") Long savingGoalId, @Param("status") PaymentStatus status);
}