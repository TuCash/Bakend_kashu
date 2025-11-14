package com.kashu.tucash.automation.domain.model.commands;

public record ChangeRecurringTransactionStatusCommand(
        Long recurringTransactionId,
        boolean active
) {
}
