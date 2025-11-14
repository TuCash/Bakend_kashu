package com.kashu.tucash.transactions.domain.model.commands;

import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionCommand(
        Long userId,
        Long accountId,
        Long categoryId,
        TransactionType type,
        BigDecimal amount,
        String description,
        LocalDate transactionDate
) {
}
