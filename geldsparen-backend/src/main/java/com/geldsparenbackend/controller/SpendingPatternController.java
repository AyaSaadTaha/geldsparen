package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.SpendingPattern;
import com.geldsparenbackend.service.SpendingPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spending-patterns")
public class SpendingPatternController {

    @Autowired
    private SpendingPatternService spendingPatternService;

    @PostMapping
    public ResponseEntity<?> createSpendingPattern(@RequestBody SpendingPattern spendingPattern, Authentication authentication) {
        try {
            String username = authentication.getName();
            SpendingPattern savedPattern = spendingPatternService.createOrUpdateSpendingPattern(spendingPattern, username);
            return ResponseEntity.ok(savedPattern);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getSpendingPattern(Authentication authentication) {
        try {
            String username = authentication.getName();
            SpendingPattern pattern = spendingPatternService.getSpendingPatternByUsername(username);
            return ResponseEntity.ok(pattern);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSpendingPattern(@PathVariable Long id, @RequestBody SpendingPattern spendingPattern, Authentication authentication) {
        try {
            String username = authentication.getName();
            SpendingPattern updatedPattern = spendingPatternService.updateSpendingPattern(id, spendingPattern, username);
            return ResponseEntity.ok(updatedPattern);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}