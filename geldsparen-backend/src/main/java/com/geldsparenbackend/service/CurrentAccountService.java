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

    public CurrentAccount createCurrentAccount(CurrentAccount currentAccount, User user) {
        // Check if the user already has a current account
        Optional<CurrentAccount> existingAccount = currentAccountRepository.findByUser(user);
        if (existingAccount.isPresent()) {
            throw new IllegalStateException("Current account already exists for this user.");
        }

        // Set the user on the new account and save it
        currentAccount.setUser(user);
        return currentAccountRepository.save(currentAccount);
    }

    public Optional<CurrentAccount> findByUser(User user) {
        return currentAccountRepository.findByUser(user);
    }
}