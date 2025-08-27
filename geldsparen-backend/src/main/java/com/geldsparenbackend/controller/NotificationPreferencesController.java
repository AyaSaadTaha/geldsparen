package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.NotificationPreferences;
import com.geldsparenbackend.service.NotificationPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification-preferences")
@CrossOrigin(origins = "*")
public class NotificationPreferencesController {
    private final NotificationPreferencesService preferencesService;

    @Autowired
    public NotificationPreferencesController(NotificationPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping
    public List<NotificationPreferences> getUserPreferences(@AuthenticationPrincipal Long userId) {
        return preferencesService.getUserPreferences(userId);
    }

    @PostMapping("/initialize")
    public ResponseEntity<String> initializePreferences(@AuthenticationPrincipal Long userId) {
        try {
            preferencesService.initializeDefaultPreferences(userId);
            return ResponseEntity.ok("Notification preferences initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error initializing preferences: " + e.getMessage());
        }
    }

    @PutMapping("/{preferenceType}")
    public NotificationPreferences updatePreference(@PathVariable String preferenceType,
                                                    @RequestParam Boolean enabled,
                                                    @AuthenticationPrincipal Long userId) {
        return preferencesService.updatePreference(userId, preferenceType, enabled);
    }

    @GetMapping("/{preferenceType}/status")
    public boolean getPreferenceStatus(@PathVariable String preferenceType,
                                       @AuthenticationPrincipal Long userId) {
        return preferencesService.isPreferenceEnabled(userId, preferenceType);
    }
}