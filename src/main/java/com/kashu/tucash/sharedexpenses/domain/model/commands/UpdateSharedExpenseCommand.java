package com.kashu.tucash.sharedexpenses.domain.model.commands;

import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SplitMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateSharedExpenseCommand(
        Long sharedExpenseId,
        String title,
        String description,
        BigDecimal totalAmount,
        String currency,
        Long categoryId,
        LocalDate expenseDate,
        SplitMethod splitMethod
) {
}
