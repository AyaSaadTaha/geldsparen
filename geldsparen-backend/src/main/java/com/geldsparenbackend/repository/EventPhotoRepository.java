package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.EventPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPhotoRepository extends JpaRepository<EventPhoto, Long> {
    List<EventPhoto> findBySavingGoalId(Long savingGoalId);

    List<EventPhoto> findByUserId(Long userId);

    List<EventPhoto> findBySavingGoalIdOrderByCreatedAtDesc(Long savingGoalId);
}