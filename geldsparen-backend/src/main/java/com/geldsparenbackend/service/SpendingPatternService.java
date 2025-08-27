package com.geldsparenbackend.service;

import com.geldsparenbackend.model.SpendingPattern;
import com.geldsparenbackend.model.SpendingPatternDetail;
import com.geldsparenbackend.repository.SpendingPatternRepository;
import com.geldsparenbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class SpendingPatternService {
    private final SpendingPatternRepository spendingPatternRepository;
    private final UserRepository userRepository;

    @Autowired
    public SpendingPatternService(SpendingPatternRepository spendingPatternRepository,
                                  UserRepository userRepository) {
        this.spendingPatternRepository = spendingPatternRepository;
        this.userRepository = userRepository;
    }

    public Optional<SpendingPattern> getUserSpendingPattern(Long userId) {
        return spendingPatternRepository.findByUserId(userId);
    }

    public SpendingPattern createOrUpdateSpendingPattern(Long userId, SpendingPattern pattern) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Optional<SpendingPattern> existingPattern = spendingPatternRepository.findByUserId(userId);

        SpendingPattern spendingPattern;
        if (existingPattern.isPresent()) {
            spendingPattern = existingPattern.get();
            spendingPattern.setFood(pattern.getFood());
            spendingPattern.setClothes(pattern.getClothes());
            spendingPattern.setMiscellaneous(pattern.getMiscellaneous());
            spendingPattern.setSavings(pattern.getSavings());
        } else {
            spendingPattern = pattern;
            spendingPattern.setUser(userRepository.findById(userId).get());
        }

        // حساب النسب المئوية
        calculatePercentages(spendingPattern);

        return spendingPatternRepository.save(spendingPattern);
    }

    private void calculatePercentages(SpendingPattern pattern) {
        BigDecimal totalIncome = pattern.getTotalIncome();
        if (totalIncome == null || totalIncome.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        pattern.getDetails().clear();

        List<SpendingCategory> categories = Arrays.asList(
                new SpendingCategory("food", pattern.getFood()),
                new SpendingCategory("clothes", pattern.getClothes()),
                new SpendingCategory("miscellaneous", pattern.getMiscellaneous()),
                new SpendingCategory("savings", pattern.getSavings())
        );

        for (SpendingCategory category : categories) {
            if (category.amount != null && category.amount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = category.amount
                        .divide(totalIncome, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

                SpendingPatternDetail detail = new SpendingPatternDetail();
                detail.setSpendingPattern(pattern);
                detail.setCategory(category.name);
                detail.setAmount(category.amount);
                detail.setPercentage(percentage);

                pattern.getDetails().add(detail);
            }
        }
    }

    public BigDecimal calculateRecommendedSavings(Long userId, BigDecimal goalAmount, int months) {
        Optional<SpendingPattern> patternOpt = spendingPatternRepository.findByUserId(userId);
        if (!patternOpt.isPresent()) {
            throw new RuntimeException("Spending pattern not found for user: " + userId);
        }

        SpendingPattern pattern = patternOpt.get();
        BigDecimal monthlySavings = goalAmount.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        // التحقق إذا كان يمكن توفير المبلغ المطلوب
        BigDecimal availableForSavings = pattern.getTotalIncome()
                .subtract(pattern.getTotalExpenses())
                .subtract(pattern.getSavings() != null ? pattern.getSavings() : BigDecimal.ZERO);

        if (monthlySavings.compareTo(availableForSavings) > 0) {
            // إذا لم يكن ممكناً، حساب العجز وتقديم توصيات
            BigDecimal deficit = monthlySavings.subtract(availableForSavings);
            throw new RuntimeException("Insufficient funds. You need to save " + deficit + " more per month.");
        }

        return monthlySavings;
    }

    private static class SpendingCategory {
        String name;
        BigDecimal amount;

        SpendingCategory(String name, BigDecimal amount) {
            this.name = name;
            this.amount = amount != null ? amount : BigDecimal.ZERO;
        }
    }
}