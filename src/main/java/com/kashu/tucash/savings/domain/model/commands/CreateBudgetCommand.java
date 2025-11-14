package com.kashu.tucash.savings.domain.model.commands;

import com.kashu.tucash.savings.domain.model.valueobjects.BudgetPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetCommand(
        Long userId,
        Long categoryId,
        BigDecimal limitAmount,
        BudgetPeriod period,
        LocalDate startDate,
        LocalDate endDate
) {
}
