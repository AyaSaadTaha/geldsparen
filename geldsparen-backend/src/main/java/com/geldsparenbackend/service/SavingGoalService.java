package com.geldsparenbackend.service;

import com.geldsparenbackend.model.SavingGoal;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.SavingGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingGoalService {

    @Autowired
    private SavingGoalRepository savingGoalRepository;

    @Autowired
    private UserService userService;

    public SavingGoal createSavingGoal(SavingGoal savingGoal, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        savingGoal.setUser(user);
        savingGoal.calculateMonthlyAmount();
        return savingGoalRepository.save(savingGoal);
    }

    public List<SavingGoal> getUserSavingGoals(String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        System.out.println("save goal" + savingGoalRepository.findByUser(user));

        return savingGoalRepository.findByUser(user);
    }

    public SavingGoal getSavingGoal(Long id, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Verify that the saving goal belongs to the authenticated user
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to access this saving goal");
        }
        return savingGoal;
    }

    public SavingGoal updateSavingGoal(Long id, SavingGoal savingGoalDetails, String username) {
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Verify that the saving goal belongs to the authenticated user
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this saving goal");
        }

        savingGoal.setName(savingGoalDetails.getName());
        savingGoal.setTargetAmount(savingGoalDetails.getTargetAmount());
        savingGoal.setDeadline(savingGoalDetails.getDeadline());
        savingGoal.setType(savingGoalDetails.getType());
        savingGoal.calculateMonthlyAmount();

        return savingGoalRepository.save(savingGoal);
    }

    public void deleteSavingGoal(Long id, String username) {
        User user = userService.findByUsername(username);
        SavingGoal savingGoal = savingGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saving goal not found"));

        // Verify that the saving goal belongs to the authenticated user
        if (!savingGoal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this saving goal");
        }

        savingGoalRepository.delete(savingGoal);
    }

}