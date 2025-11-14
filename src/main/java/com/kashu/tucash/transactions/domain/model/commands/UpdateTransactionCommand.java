package com.kashu.tucash.transactions.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionCommand(
        Long transactionId,
        Long accountId,
        Long categoryId,
        BigDecimal amount,
        String description,
        LocalDate transactionDate
) {
}
