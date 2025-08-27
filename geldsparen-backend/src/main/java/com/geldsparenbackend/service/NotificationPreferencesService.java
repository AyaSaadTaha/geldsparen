package com.geldsparenbackend.service;

import com.geldsparenbackend.model.NotificationPreferences;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.NotificationPreferencesRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationPreferencesService {
    private final NotificationPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationPreferencesService(NotificationPreferencesRepository preferencesRepository,
                                          UserRepository userRepository) {
        this.preferencesRepository = preferencesRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationPreferences> getUserPreferences(Long userId) {
        return preferencesRepository.findByUserId(userId);
    }

    public void initializeDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // إنشاء التفضيلات الافتراضية
        Arrays.stream(NotificationPreferences.PreferenceType.values()).forEach(preferenceType -> {
            if (!preferencesRepository.existsByUserIdAndPreferenceType(userId, preferenceType.name())) {
                NotificationPreferences preference = new NotificationPreferences();
                preference.setUser(user);
                preference.setPreferenceType(preferenceType.name());

                // تعطيل بعض التفضيلات افتراضياً
                if (preferenceType == NotificationPreferences.PreferenceType.PROMOTIONAL) {
                    preference.setEnabled(false);
                }

                preferencesRepository.save(preference);
            }
        });
    }

    public NotificationPreferences updatePreference(Long userId, String preferenceType, Boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        NotificationPreferences preference = preferencesRepository
                .findByUserIdAndPreferenceType(userId, preferenceType)
                .orElseGet(() -> {
                    NotificationPreferences newPreference = new NotificationPreferences();
                    newPreference.setUser(user);
                    newPreference.setPreferenceType(preferenceType);
                    return newPreference;
                });

        preference.setEnabled(enabled);
        return preferencesRepository.save(preference);
    }

    public boolean isPreferenceEnabled(Long userId, String preferenceType) {
        return preferencesRepository.findByUserIdAndPreferenceType(userId, preferenceType)
                .map(NotificationPreferences::getEnabled)
                .orElse(false);
    }

    public boolean canReceivePaymentReminders(Long userId) {
        return isPreferenceEnabled(userId, NotificationPreferences.PreferenceType.PAYMENT_REMINDERS.name());
    }

    public boolean canReceiveBudgetAlerts(Long userId) {
        return isPreferenceEnabled(userId, NotificationPreferences.PreferenceType.BUDGET_ALERTS.name());
    }
}