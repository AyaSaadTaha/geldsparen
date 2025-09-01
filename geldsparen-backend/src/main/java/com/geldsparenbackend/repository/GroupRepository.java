package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Query("SELECT g FROM Group g WHERE g.savingGoal = :savingGoal")
    Group findBySavingGoals(@Param("savingGoal") SavingGoal savingGoal);

    Optional<Group> findBySavingGoal(SavingGoal savingGoal);

    // الطريقة الصحيحة للبحث بـ savingGoalId
    @Query("SELECT g FROM Group g WHERE g.savingGoal.id = :savingGoalId")
    Optional<Group> findBySavingGoalId(@Param("savingGoalId") Long savingGoalId);

    List<Group> findByCreatedBy(User user);

    @Query("SELECT g FROM Group g WHERE g.name LIKE %:name%")
    List<Group> findByNameContaining(@Param("name") String name);

    Boolean existsByName(String name);

    // طريقة إضافية للتحقق من وجود مجموعة بـ savingGoalId
    @Query("SELECT COUNT(g) > 0 FROM Group g WHERE g.savingGoal.id = :savingGoalId")
    Boolean existsBySavingGoalId(@Param("savingGoalId") Long savingGoalId);

    @Query("SELECT g FROM Group g WHERE g.createdBy.id = :userId")
    List<Group> findByCreatedBy(@Param("userId") Long userId);

    @Query("SELECT g FROM Group g JOIN g.members gm WHERE gm.user.id = :userId AND gm.invitationStatus = 'ACCEPTED'")
    List<Group> findByMemberId(@Param("userId") Long userId);
}