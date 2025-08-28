package com.geldsparenbackend.service;

import com.geldsparenbackend.model.CurrentAccount;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.repository.CurrentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentAccountService {

    @Autowired
    private CurrentAccountRepository currentAccountRepository;

    @Autowired
    private UserService userService;

    public CurrentAccount createCurrentAccount(CurrentAccount currentAccount, String username) {
        User user = userService.findByUsername(username);

        // Check if user already has a current account
        if (currentAccountRepository.existsByUser(user)) {
            throw new RuntimeException("User already has a current account");
        }

        currentAccount.setUser(user);

        // Generate IBAN if not provided
        if (currentAccount.getIban() == null || currentAccount.getIban().isEmpty()) {
            currentAccount.setIban(generateIban());
        }

        return currentAccountRepository.save(currentAccount);
    }

    public CurrentAccount getCurrentAccountByUsername(String username) {
        User user = userService.findByUsername(username);
        return currentAccountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Current account not found for user: " + username));
    }

    public CurrentAccount updateCurrentAccount(Long id, CurrentAccount currentAccountDetails, String username) {
        User user = userService.findByUsername(username);
        CurrentAccount currentAccount = currentAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Current account not found"));

        // Verify that the current account belongs to the authenticated user
        if (!currentAccount.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this account");
        }

        currentAccount.setSalary(currentAccountDetails.getSalary());
        currentAccount.setPayday(currentAccountDetails.getPayday());

        return currentAccountRepository.save(currentAccount);
    }

    private String generateIban() {
        // Simple IBAN generation for demo purposes
        return "DE89 3704 0044 0532 0130 00" + (1000 + (int)(Math.random() * 9000));
    }

}