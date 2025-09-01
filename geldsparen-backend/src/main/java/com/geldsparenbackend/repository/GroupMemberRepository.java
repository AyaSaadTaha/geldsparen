package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.Group;
import com.geldsparenbackend.model.GroupMember;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.model.GroupMember.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    // NEW: Find by user and invitation status
    List<GroupMember> findByUserAndInvitationStatus(User user, GroupMember.InvitationStatus invitationStatus);

    // NEW: Find by email and invitation status (for users who haven't registered yet)
    List<GroupMember> findByEmailAndInvitationStatus(String email, GroupMember.InvitationStatus invitationStatus);

    // NEW: Count pending invitations for a user
    Long countByUserAndInvitationStatus(User user, GroupMember.InvitationStatus invitationStatus);


    List<GroupMember> findByGroup(Group group);
    List<GroupMember> findByUser(User user);
    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    List<GroupMember> findByInvitationStatus(InvitationStatus status);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.invitationStatus = 'PENDING'")
    List<GroupMember> findPendingInvitationsByGroup(@Param("group") Group group);

    Long countByGroupAndInvitationStatus(Group group, InvitationStatus status);

    Boolean existsByGroupAndUser(Group group, User user);

    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.group = :group")
    void deleteByGroup(@Param("group") Group group);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.invitationStatus = 'ACCEPTED'")
    List<GroupMember> findAcceptedMembersByGroup(@Param("group") Group group);


    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.user.id = :userId")
    Optional<GroupMember> findByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.user.id = :userId")
    List<GroupMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<GroupMember> findByGroupId(@Param("groupId") Long groupId);

    // Methode für Gruppenmitglieder nach SavingGoal (wenn Group eine savingGoal Referenz hat)
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.savingGoal.id = :savingGoalId")
    List<GroupMember> findBySavingGoalId(@Param("savingGoalId") Long savingGoalId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.invitationStatus = 'ACCEPTED'")
    List<GroupMember> findAcceptedMembersByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.invitationStatus = 'PENDING'")
    List<GroupMember> findPendingMembersByGroupId(@Param("groupId") Long groupId);

}