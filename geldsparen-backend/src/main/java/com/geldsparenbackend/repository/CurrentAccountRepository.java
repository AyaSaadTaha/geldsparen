package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.CurrentAccount;
import com.geldsparenbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrentAccountRepository extends JpaRepository<CurrentAccount, Long> {
    Optional<CurrentAccount> findByUser(User user);
    Optional<CurrentAccount> findByIban(String iban);
    Boolean existsByUser(User user);
}