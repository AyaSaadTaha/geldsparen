package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.SpendingPattern;
import com.geldsparenbackend.service.SpendingPatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/spending-patterns")
@CrossOrigin(origins = "*")
public class SpendingPatternController {
    private final SpendingPatternService spendingPatternService;

    @Autowired
    public SpendingPatternController(SpendingPatternService spendingPatternService) {
        this.spendingPatternService = spendingPatternService;
    }

    @GetMapping
    public ResponseEntity<SpendingPattern> getSpendingPattern(@AuthenticationPrincipal Long userId) {
        Optional<SpendingPattern> pattern = spendingPatternService.getUserSpendingPattern(userId);
        return pattern.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public SpendingPattern createOrUpdateSpendingPattern(@Valid @RequestBody SpendingPattern pattern,
                                                         @AuthenticationPrincipal Long userId) {
        return spendingPatternService.createOrUpdateSpendingPattern(userId, pattern);
    }

    @GetMapping("/calculate-savings")
    public ResponseEntity<BigDecimal> calculateRecommendedSavings(
            @RequestParam BigDecimal goalAmount,
            @RequestParam int months,
            @AuthenticationPrincipal Long userId) {
        try {
            BigDecimal monthlySavings = spendingPatternService.calculateRecommendedSavings(userId, goalAmount, months);
            return ResponseEntity.ok(monthlySavings);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}