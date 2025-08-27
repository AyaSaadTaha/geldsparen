package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.service.SavingGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/saving-goals")
@CrossOrigin(origins = "*")
public class SavingGoalController {
    private final SavingGoalService savingGoalService;

    @Autowired
    public SavingGoalController(SavingGoalService savingGoalService) {
        this.savingGoalService = savingGoalService;
    }

    @GetMapping
    public List<SavingGoal> getUserSavingGoals(@AuthenticationPrincipal Long userId) {
        return savingGoalService.getUserSavingGoals(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingGoal> getSavingGoal(@PathVariable Long id,
                                                    @AuthenticationPrincipal Long userId) {
        Optional<SavingGoal> savingGoal = savingGoalService.getSavingGoalById(id);

        if (savingGoal.isPresent() && savingGoal.get().getUser().getId().equals(userId)) {
            return ResponseEntity.ok(savingGoal.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public SavingGoal createSavingGoal(@Valid @RequestBody SavingGoal savingGoal,
                                       @AuthenticationPrincipal Long userId) {
        return savingGoalService.createSavingGoal(savingGoal, userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingGoal> updateSavingGoal(@PathVariable Long id,
                                                       @Valid @RequestBody SavingGoal savingGoalDetails,
                                                       @AuthenticationPrincipal Long userId) {
        Optional<SavingGoal> existingGoal = savingGoalService.getSavingGoalById(id);

        if (existingGoal.isPresent() && existingGoal.get().getUser().getId().equals(userId)) {
            SavingGoal updatedGoal = savingGoalService.updateSavingGoal(id, savingGoalDetails);
            return ResponseEntity.ok(updatedGoal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSavingGoal(@PathVariable Long id,
                                                 @AuthenticationPrincipal Long userId) {
        Optional<SavingGoal> existingGoal = savingGoalService.getSavingGoalById(id);

        if (existingGoal.isPresent() && existingGoal.get().getUser().getId().equals(userId)) {
            savingGoalService.deleteSavingGoal(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}