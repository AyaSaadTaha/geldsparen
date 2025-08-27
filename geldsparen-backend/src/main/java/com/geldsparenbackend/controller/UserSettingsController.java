package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.UserSettings;
import com.geldsparenbackend.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user-settings")
@CrossOrigin(origins = "*")
public class UserSettingsController {
    private final UserSettingsService userSettingsService;

    @Autowired
    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public ResponseEntity<UserSettings> getUserSettings(@AuthenticationPrincipal Long userId) {
        Optional<UserSettings> settings = userSettingsService.getUserSettings(userId);

        if (settings.isPresent()) {
            return ResponseEntity.ok(settings.get());
        } else {
            // إنشاء إعدادات افتراضية إذا لم تكن موجودة
            UserSettings defaultSettings = userSettingsService.createDefaultSettings(userId);
            return ResponseEntity.ok(defaultSettings);
        }
    }

    @PutMapping
    public UserSettings updateUserSettings(@RequestBody UserSettings settingsDetails,
                                           @AuthenticationPrincipal Long userId) {
        return userSettingsService.updateUserSettings(userId, settingsDetails);
    }

    @GetMapping("/can-receive-notifications")
    public boolean canReceiveNotifications(@AuthenticationPrincipal Long userId) {
        return userSettingsService.canReceiveNotifications(userId);
    }

    @GetMapping("/can-receive-emails")
    public boolean canReceiveEmailNotifications(@AuthenticationPrincipal Long userId) {
        return userSettingsService.canReceiveEmailNotifications(userId);
    }

    @GetMapping("/can-receive-payment-reminders")
    public boolean canReceivePaymentReminders(@AuthenticationPrincipal Long userId) {
        return userSettingsService.canReceivePaymentReminders(userId);
    }
}