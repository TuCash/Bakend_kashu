package com.kashu.tucash.savings.interfaces.rest.resources;

import java.math.BigDecimal;

public record UpdateGoalResource(
        BigDecimal targetAmount,
        String deadline
) {
}
