package com.kashu.tucash.transactions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionResource(
        Long accountId,
        Long categoryId,
        BigDecimal amount,
        String description,
        LocalDate transactionDate
) {
}
