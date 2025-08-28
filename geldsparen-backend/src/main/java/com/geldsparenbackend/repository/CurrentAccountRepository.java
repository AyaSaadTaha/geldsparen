package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.CurrentAccount;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {
    // Custom method to find a CurrentAccount by a User object
    Optional<CurrentAccount> findByUser(User user);
}