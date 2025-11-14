package com.kashu.tucash.transactions.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.Date;

public record AccountResource(
        Long id,
        String name,
        String currency,
        BigDecimal balance,
        Date createdAt,
        Date updatedAt
) {
}
