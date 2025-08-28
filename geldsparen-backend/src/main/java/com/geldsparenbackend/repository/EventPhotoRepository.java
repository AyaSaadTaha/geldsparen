package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.EventPhoto;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {
    List<EventPhoto> findBySavingGoal(SavingGoal savingGoal);
    List<EventPhoto> findByUser(User user);

    Long countBySavingGoal(SavingGoal savingGoal);
}