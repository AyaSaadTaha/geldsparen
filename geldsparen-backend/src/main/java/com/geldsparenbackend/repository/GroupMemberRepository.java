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
    List<GroupMember> findByGroup(Group group);
    List<GroupMember> findByUser(User user);
    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    List<GroupMember> findByInvitationStatus(InvitationStatus status);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.invitationStatus = 'PENDING'")
    List<GroupMember> findPendingInvitationsByGroup(@Param("group") Group group);

    Long countByGroupAndInvitationStatus(Group group, InvitationStatus status);

    Boolean existsByGroupAndUser(Group group, User user);

    // طريقة لحذف جميع أعضاء مجموعة
    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.group = :group")
    void deleteByGroup(@Param("group") Group group);

    // طريقة للبحث بالأعضاء المقبولين فقط
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.invitationStatus = 'ACCEPTED'")
    List<GroupMember> findAcceptedMembersByGroup(@Param("group") Group group);
}