package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.EventReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
    List<EventReview> findBySavingGoalId(Long savingGoalId);

    Optional<EventReview> findBySavingGoalIdAndUserId(Long savingGoalId, Long userId);

    @Query("SELECT AVG(er.rating) FROM EventReview er WHERE er.savingGoal.id = :savingGoalId")
    Double getAverageRatingBySavingGoalId(@Param("savingGoalId") Long savingGoalId);

    @Query("SELECT COUNT(er) FROM EventReview er WHERE er.savingGoal.id = :savingGoalId")
    Integer getReviewCountBySavingGoalId(@Param("savingGoalId") Long savingGoalId);
}