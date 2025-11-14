package com.kashu.tucash.dashboard.interfaces.rest.resources;

import java.math.BigDecimal;

public record DashboardPulseResource(
        String currency,
        String periodLabel,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal balance,
        BigDecimal savingsRate
) {
}
