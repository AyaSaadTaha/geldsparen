package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.SpendingPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpendingPatternRepository extends JpaRepository<SpendingPattern, Long> {
    Optional<SpendingPattern> findByUserId(Long userId);

    @Query("SELECT sp FROM SpendingPattern sp WHERE sp.user.id = :userId")
    Optional<SpendingPattern> findWithDetailsByUserId(@Param("userId") Long userId);
}