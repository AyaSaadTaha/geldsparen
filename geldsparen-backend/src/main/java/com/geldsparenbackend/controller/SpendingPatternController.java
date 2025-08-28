package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.SpendingPattern;
import com.geldsparenbackend.service.SpendingPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spending-patterns")
public class SpendingPatternController {

    @Autowired
    private SpendingPatternService spendingPatternService;

    @PostMapping
    public ResponseEntity<SpendingPattern> createSpendingPattern(@RequestBody SpendingPattern spendingPattern, Authentication authentication) {
        String username = authentication.getName();
        SpendingPattern savedPattern = spendingPatternService.createSpendingPattern(spendingPattern, username);
        return ResponseEntity.ok(savedPattern);
    }

    @GetMapping
    public ResponseEntity<SpendingPattern> getSpendingPattern(Authentication authentication) {
        String username = authentication.getName();
        SpendingPattern pattern = spendingPatternService.getSpendingPatternByUsername(username);
        return ResponseEntity.ok(pattern);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpendingPattern> updateSpendingPattern(@PathVariable Long id, @RequestBody SpendingPattern spendingPattern, Authentication authentication) {
        String username = authentication.getName();
        SpendingPattern updatedPattern = spendingPatternService.updateSpendingPattern(id, spendingPattern, username);
        return ResponseEntity.ok(updatedPattern);
    }
}