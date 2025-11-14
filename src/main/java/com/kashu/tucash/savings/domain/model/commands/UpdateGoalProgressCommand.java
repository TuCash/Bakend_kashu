package com.kashu.tucash.savings.domain.model.commands;

import java.math.BigDecimal;

public record UpdateGoalProgressCommand(
        Long goalId,
        BigDecimal currentAmount
) {
}
