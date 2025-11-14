package com.kashu.tucash.automation.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringTransactionResource(
        Long id,
        Long accountId,
        Long categoryId,
        String categoryName,
        String type,
        BigDecimal amount,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String frequency,
        LocalDate nextExecutionDate,
        Boolean active
) {
}
