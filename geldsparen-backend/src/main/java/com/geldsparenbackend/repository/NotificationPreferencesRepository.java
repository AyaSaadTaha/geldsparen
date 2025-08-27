package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    List<NotificationPreferences> findByUserId(Long userId);

    Optional<NotificationPreferences> findByUserIdAndPreferenceType(Long userId, String preferenceType);

    @Query("SELECT np FROM NotificationPreferences np WHERE np.user.id = :userId AND np.enabled = true")
    List<NotificationPreferences> findEnabledPreferencesByUserId(@Param("userId") Long userId);

    boolean existsByUserIdAndPreferenceType(Long userId, String preferenceType);
}