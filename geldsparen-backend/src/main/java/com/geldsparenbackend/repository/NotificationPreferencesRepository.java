package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.NotificationPreferences;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    List<NotificationPreferences> findByUser(User user);
    Optional<NotificationPreferences> findByUserAndPreferenceType(User user, String preferenceType);

    Boolean existsByUserAndPreferenceTypeAndEnabled(User user, String preferenceType, Boolean enabled);
}