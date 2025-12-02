package com.kashu.tucash.transactions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record TransactionResource(
        Long id,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        String categoryIcon,
        String type,
        BigDecimal amount,
        String description,
        LocalDate transactionDate,
        Long goalId,
        String goalName,
        Date createdAt,
        Date updatedAt
) {
}
