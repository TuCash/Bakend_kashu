package com.kashu.tucash.dashboard.domain.services;

import com.kashu.tucash.dashboard.interfaces.rest.resources.*;

import java.time.LocalDate;

public interface DashboardQueryService {
    DashboardPulseResource getPulse(Long userId, LocalDate startDate, LocalDate endDate);
    TrendSeriesResource getTrends(Long userId, int months);
    CategoryLeaksResource getLeaks(Long userId, LocalDate startDate, LocalDate endDate, int top);
}
