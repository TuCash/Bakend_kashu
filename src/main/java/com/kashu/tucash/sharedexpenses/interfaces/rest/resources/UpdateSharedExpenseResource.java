package com.kashu.tucash.sharedexpenses.interfaces.rest.resources;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateSharedExpenseResource(
        String title,
        String description,

        @Positive(message = "Total amount must be positive")
        BigDecimal totalAmount,

        String currency,
        Long categoryId,
        LocalDate expenseDate,
        String splitMethod
) {
}
