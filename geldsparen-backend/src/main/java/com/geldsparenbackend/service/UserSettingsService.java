package com.geldsparenbackend.service;

import com.geldsparenbackend.model.User;
import com.geldsparenbackend.model.UserSettings;
import com.geldsparenbackend.repository.UserRepository;
import com.geldsparenbackend.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSettingsService {
    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserSettingsService(UserSettingsRepository userSettingsRepository,
                               UserRepository userRepository) {
        this.userSettingsRepository = userSettingsRepository;
        this.userRepository = userRepository;
    }

    public Optional<UserSettings> getUserSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId);
    }

    public UserSettings createDefaultSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (userSettingsRepository.existsByUserId(userId)) {
            throw new RuntimeException("User settings already exist for user: " + userId);
        }

        UserSettings settings = new UserSettings();
        settings.setUser(user);
        // جميع القيم الافتراضية محددة في الـ Entity

        return userSettingsRepository.save(settings);
    }

    public UserSettings updateUserSettings(Long userId, UserSettings settingsDetails) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User settings not found for user: " + userId));

        if (settingsDetails.getNotificationEnabled() != null) {
            settings.setNotificationEnabled(settingsDetails.getNotificationEnabled());
        }

        if (settingsDetails.getEmailNotifications() != null) {
            settings.setEmailNotifications(settingsDetails.getEmailNotifications());
        }

        if (settingsDetails.getPushNotifications() != null) {
            settings.setPushNotifications(settingsDetails.getPushNotifications());
        }

        if (settingsDetails.getLanguage() != null) {
            settings.setLanguage(settingsDetails.getLanguage());
        }

        if (settingsDetails.getCurrency() != null) {
            settings.setCurrency(settingsDetails.getCurrency());
        }

        if (settingsDetails.getTheme() != null) {
            settings.setTheme(settingsDetails.getTheme());
        }

        if (settingsDetails.getMonthlyBudgetNotification() != null) {
            settings.setMonthlyBudgetNotification(settingsDetails.getMonthlyBudgetNotification());
        }

        if (settingsDetails.getPaymentReminderNotification() != null) {
            settings.setPaymentReminderNotification(settingsDetails.getPaymentReminderNotification());
        }

        if (settingsDetails.getLowBalanceAlert() != null) {
            settings.setLowBalanceAlert(settingsDetails.getLowBalanceAlert());
        }

        if (settingsDetails.getAlertThreshold() != null) {
            settings.setAlertThreshold(settingsDetails.getAlertThreshold());
        }

        return userSettingsRepository.save(settings);
    }

    public boolean canReceiveNotifications(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(settings -> Boolean.TRUE.equals(settings.getNotificationEnabled()))
                .orElse(false);
    }

    public boolean canReceiveEmailNotifications(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(settings -> Boolean.TRUE.equals(settings.getNotificationEnabled()) &&
                        Boolean.TRUE.equals(settings.getEmailNotifications()))
                .orElse(false);
    }

    public boolean canReceivePaymentReminders(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .map(settings -> Boolean.TRUE.equals(settings.getNotificationEnabled()) &&
                        Boolean.TRUE.equals(settings.getPaymentReminderNotification()))
                .orElse(false);
    }
}