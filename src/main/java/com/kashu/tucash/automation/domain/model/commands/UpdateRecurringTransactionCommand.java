package com.kashu.tucash.automation.domain.model.commands;

import com.kashu.tucash.automation.domain.model.valueobjects.RecurringFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateRecurringTransactionCommand(
        Long recurringTransactionId,
        BigDecimal amount,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        RecurringFrequency frequency
) {
}
