package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByGroupId(Long groupId);

    List<GroupMember> findByUserId(Long userId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.invitationStatus = 'PENDING'")
    List<GroupMember> findPendingInvitationsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.invitationStatus = 'ACCEPTED'")
    Integer countAcceptedMembersByGroupId(@Param("groupId") Long groupId);
}