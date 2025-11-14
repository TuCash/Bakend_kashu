package com.kashu.tucash.automation.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionResource(
        @NotNull Long accountId,
        @NotNull Long categoryId,
        @NotNull String type,
        @NotNull BigDecimal amount,
        String description,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        @NotNull String frequency
) {
}
