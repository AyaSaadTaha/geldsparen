package com.geldsparenbackend.service;

import com.geldsparenbackend.model.SpendingPattern;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.SpendingPatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpendingPatternService {

    @Autowired
    private SpendingPatternRepository spendingPatternRepository;

    @Autowired
    private UserService userService;

    public SpendingPattern createSpendingPattern(SpendingPattern spendingPattern, String username) {
        User user = userService.findByUsername(username);

        // Check if user already has a spending pattern
        if (spendingPatternRepository.existsByUser(user)) {
            throw new RuntimeException("User already has a spending pattern");
        }

        spendingPattern.setUser(user);
        return spendingPatternRepository.save(spendingPattern);
    }

    public SpendingPattern getSpendingPatternByUsername(String username) {
        User user = userService.findByUsername(username);
        return spendingPatternRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Spending pattern not found for user: " + username));
    }

    public SpendingPattern updateSpendingPattern(Long id, SpendingPattern spendingPatternDetails, String username) {
        User user = userService.findByUsername(username);
        SpendingPattern spendingPattern = spendingPatternRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spending pattern not found"));

        // Verify that the spending pattern belongs to the authenticated user
        if (!spendingPattern.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this spending pattern");
        }

        spendingPattern.setFood(spendingPatternDetails.getFood());
        spendingPattern.setClothes(spendingPatternDetails.getClothes());
        spendingPattern.setMiscellaneous(spendingPatternDetails.getMiscellaneous());
        spendingPattern.setSavings(spendingPatternDetails.getSavings());

        return spendingPatternRepository.save(spendingPattern);
    }
}