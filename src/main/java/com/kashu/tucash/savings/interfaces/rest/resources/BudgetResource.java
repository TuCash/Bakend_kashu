package com.kashu.tucash.savings.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetResource(
        Long id,
        Long categoryId,
        String categoryName,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        BigDecimal spentPercentage,
        String period,
        LocalDate startDate,
        LocalDate endDate,
        boolean isWarning,
        boolean isExceeded,
        boolean isActive
) {
}
