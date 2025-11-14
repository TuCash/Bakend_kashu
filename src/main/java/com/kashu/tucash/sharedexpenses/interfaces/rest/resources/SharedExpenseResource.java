package com.kashu.tucash.sharedexpenses.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record SharedExpenseResource(
        Long id,
        Long creatorId,
        String creatorName,
        String title,
        String description,
        BigDecimal totalAmount,
        String currency,
        Long categoryId,
        String categoryName,
        String categoryIcon,
        LocalDate expenseDate,
        String status,
        String splitMethod,
        LocalDateTime settledAt,
        List<ParticipantResource> participants,
        Date createdAt,
        Date updatedAt
) {
}
