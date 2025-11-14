package com.kashu.tucash.dashboard.interfaces.rest.resources;

import java.math.BigDecimal;

public record CategoryLeakResource(
        Long categoryId,
        String categoryName,
        String categoryIcon,
        BigDecimal amount,
        BigDecimal percentage,
        String color
) {
}
