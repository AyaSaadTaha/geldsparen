package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.service.SavingGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saving-goals")
public class SavingGoalController {

    @Autowired
    private SavingGoalService savingGoalService;

    @PostMapping
    public ResponseEntity<SavingGoal> createSavingGoal(@RequestBody SavingGoal savingGoal, Authentication authentication) {
        String username = authentication.getName();
        SavingGoal savedGoal = savingGoalService.createSavingGoal(savingGoal, username);
        return ResponseEntity.ok(savedGoal);
    }

    @GetMapping
    public ResponseEntity<List<SavingGoal>> getUserSavingGoals(Authentication authentication) {
        String username = authentication.getName();
        List<SavingGoal> goals = savingGoalService.getUserSavingGoals(username);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingGoal> getSavingGoal(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        SavingGoal goal = savingGoalService.getSavingGoal(id, username);
        return ResponseEntity.ok(goal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingGoal> updateSavingGoal(@PathVariable Long id, @RequestBody SavingGoal savingGoal, Authentication authentication) {
        String username = authentication.getName();
        SavingGoal updatedGoal = savingGoalService.updateSavingGoal(id, savingGoal, username);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavingGoal(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        savingGoalService.deleteSavingGoal(id, username);
        return ResponseEntity.ok().build();
    }
}