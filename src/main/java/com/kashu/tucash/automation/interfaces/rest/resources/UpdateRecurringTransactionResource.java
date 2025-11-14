package com.kashu.tucash.automation.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateRecurringTransactionResource(
        BigDecimal amount,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String frequency
) {
}
