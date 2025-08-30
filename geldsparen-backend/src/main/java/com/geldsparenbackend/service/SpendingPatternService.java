package com.geldsparenbackend.service;

import com.geldsparenbackend.model.CurrentAccount;
import com.geldsparenbackend.model.SpendingPattern;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.SpendingPatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class SpendingPatternService {

    @Autowired
    private SpendingPatternRepository spendingPatternRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrentAccountService currentAccountService;

    public SpendingPattern createOrUpdateSpendingPattern(SpendingPattern spendingPattern, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Check if user already has a spending pattern
        Optional<SpendingPattern> existingPattern = spendingPatternRepository.findByUser(user);

        CurrentAccount acc = currentAccountService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current account not found"));

        BigDecimal totalExpenses = spendingPattern.getFood()
                .add(spendingPattern.getClothes())
                .add(spendingPattern.getMiscellaneous())
                .add(spendingPattern.getRenter());

        spendingPattern.setTotal_expenses(totalExpenses);
        spendingPattern.setTotal_income(acc.getSalary());
        spendingPattern.setSavings(spendingPattern.getTotal_income().subtract(totalExpenses));
        spendingPattern.setUser(user);

        if (existingPattern.isPresent()) {
            // Update existing pattern
            SpendingPattern patternToUpdate = existingPattern.get();
            patternToUpdate.setFood(spendingPattern.getFood());
            patternToUpdate.setClothes(spendingPattern.getClothes());
            patternToUpdate.setRenter(spendingPattern.getRenter());
            patternToUpdate.setMiscellaneous(spendingPattern.getMiscellaneous());
            patternToUpdate.setTotal_expenses(totalExpenses);
            patternToUpdate.setSavings(spendingPattern.getSavings());
            return spendingPatternRepository.save(patternToUpdate);
        } else {
            // Create new pattern
            return spendingPatternRepository.save(spendingPattern);
        }
    }

    public SpendingPattern getSpendingPatternByUsername(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return spendingPatternRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Spending pattern not found for user: " + username));
    }

    public SpendingPattern updateSpendingPattern(Long id, SpendingPattern spendingPatternDetails, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SpendingPattern spendingPattern = spendingPatternRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spending pattern not found"));

        // Verify that the spending pattern belongs to the authenticated user
        if (!spendingPattern.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this spending pattern");
        }

        // Calculate new values
        BigDecimal totalExpenses = spendingPatternDetails.getFood()
                .add(spendingPatternDetails.getClothes())
                .add(spendingPatternDetails.getRenter())
                .add(spendingPatternDetails.getMiscellaneous());

        CurrentAccount acc = currentAccountService.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current account not found"));

        spendingPattern.setFood(spendingPatternDetails.getFood());
        spendingPattern.setClothes(spendingPatternDetails.getClothes());
        spendingPattern.setClothes(spendingPatternDetails.getRenter());
        spendingPattern.setMiscellaneous(spendingPatternDetails.getMiscellaneous());
        spendingPattern.setTotal_expenses(totalExpenses);
        spendingPattern.setTotal_income(acc.getSalary());
        spendingPattern.setSavings(acc.getSalary().subtract(totalExpenses));

        return spendingPatternRepository.save(spendingPattern);
    }
}