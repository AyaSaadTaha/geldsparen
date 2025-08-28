package com.geldsparenbackend.repository;

import com.geldsparenbackend.model.SpendingPatternDetail;
import com.geldsparenbackend.model.SpendingPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpendingPatternDetailRepository extends JpaRepository<SpendingPatternDetail, Long> {
    List<SpendingPatternDetail> findBySpendingPattern(SpendingPattern spendingPattern);
    List<SpendingPatternDetail> findByCategory(String category);

    void deleteBySpendingPattern(SpendingPattern spendingPattern);
}