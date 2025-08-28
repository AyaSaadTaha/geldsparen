package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.CurrentAccount;
import com.geldsparenbackend.model.User;
import com.geldsparenbackend.service.CurrentAccountService;
import com.geldsparenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/current-accounts")
public class CurrentAccountController {

    @Autowired
    private CurrentAccountService currentAccountService;

    @Autowired
    private UserService userService;

    // DTO for incoming data
    public static class CurrentAccountRequest {
        public BigDecimal salary;
        public Integer payday;
        public String iban;
    }

    @PostMapping
    public ResponseEntity<CurrentAccount> createCurrentAccount(@RequestBody CurrentAccountRequest request, Principal principal) {
        // Get the authenticated user
        User user = userService.getUserByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        CurrentAccount newAccount = new CurrentAccount();
        newAccount.setSalary(request.salary);
        newAccount.setPayday(request.payday);
        newAccount.setIban(request.iban);

        try {
            CurrentAccount createdAccount = currentAccountService.createCurrentAccount(newAccount, user);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // Handle the case where the account already exists
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}