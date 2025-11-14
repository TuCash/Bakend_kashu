package com.kashu.tucash.automation.domain.model.commands;

import com.kashu.tucash.automation.domain.model.valueobjects.RecurringFrequency;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionCommand(
        Long userId,
        Long accountId,
        Long categoryId,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        RecurringFrequency frequency
) {
}
