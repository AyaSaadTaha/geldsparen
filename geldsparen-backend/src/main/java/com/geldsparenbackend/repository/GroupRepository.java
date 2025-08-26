package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findBySavingGoalId(Long savingGoalId);

    @Query("SELECT g FROM Group g JOIN g.members gm WHERE gm.user.id = :userId")
    List<Group> findByMemberUserId(@Param("userId") Long userId);

    @Query("SELECT g FROM Group g WHERE g.createdBy.id = :userId")
    List<Group> findByCreatedBy(@Param("userId") Long userId);
}