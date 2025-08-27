package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId);

    @Query("SELECT us FROM UserSettings us WHERE us.user.id = :userId")
    Optional<UserSettings> findWithUserByUserId(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);
}