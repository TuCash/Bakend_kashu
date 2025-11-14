package com.kashu.tucash.dashboard.interfaces.rest.resources;

import java.math.BigDecimal;

public record MonthlyTrendResource(
        String month,
        BigDecimal income,
        BigDecimal expenses,
        BigDecimal balance
) {
}
