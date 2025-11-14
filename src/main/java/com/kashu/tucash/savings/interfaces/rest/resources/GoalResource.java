package com.kashu.tucash.savings.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record GoalResource(
        Long id,
        String name,
        String description,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal progressPercentage,
        LocalDate deadline,
        String status,
        Date celebratedAt,
        Date createdAt,
        Date updatedAt
) {
}
