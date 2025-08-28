package com.geldsparenbackend.controller;

import com.geldsparenbackend.model.CurrentAccount;
import com.geldsparenbackend.service.CurrentAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/current-accounts")
public class CurrentAccountController {

    @Autowired
    private CurrentAccountService currentAccountService;

    @PostMapping
    public ResponseEntity<CurrentAccount> createCurrentAccount(@RequestBody CurrentAccount currentAccount, Authentication authentication) {
        String username = authentication.getName();
        CurrentAccount savedAccount = currentAccountService.createCurrentAccount(currentAccount, username);
        return ResponseEntity.ok(savedAccount);
    }

    @GetMapping
    public ResponseEntity<CurrentAccount> getCurrentAccount(Authentication authentication) {
        String username = authentication.getName();
        CurrentAccount account = currentAccountService.getCurrentAccountByUsername(username);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurrentAccount> updateCurrentAccount(@PathVariable Long id, @RequestBody CurrentAccount currentAccount, Authentication authentication) {
        String username = authentication.getName();
        CurrentAccount updatedAccount = currentAccountService.updateCurrentAccount(id, currentAccount, username);
        return ResponseEntity.ok(updatedAccount);
    }
}