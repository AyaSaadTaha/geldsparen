package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.EventReview;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
    List<EventReview> findBySavingGoal(SavingGoal savingGoal);
    List<EventReview> findByUser(User user);

    @Query("SELECT AVG(er.rating) FROM EventReview er WHERE er.savingGoal = :savingGoal")
    Optional<Double> getAverageRatingBySavingGoal(@Param("savingGoal") SavingGoal savingGoal);

    @Query("SELECT COUNT(er) FROM EventReview er WHERE er.savingGoal = :savingGoal AND er.rating >= :minRating")
    Long countBySavingGoalAndMinRating(@Param("savingGoal") SavingGoal savingGoal,
                                       @Param("minRating") Integer minRating);

    Boolean existsBySavingGoalAndUser(SavingGoal savingGoal, User user);
}