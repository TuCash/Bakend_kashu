package com.kashu.tucash.dashboard.interfaces.rest.resources;

import java.util.List;

public record TrendSeriesResource(
        String currency,
        List<MonthlyTrendResource> series
) {
}
