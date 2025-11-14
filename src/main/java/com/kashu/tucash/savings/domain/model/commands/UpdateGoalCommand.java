package com.kashu.tucash.savings.domain.model.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateGoalCommand(
        Long goalId,
        String name,
        String description,
        BigDecimal targetAmount,
        LocalDate deadline
) {
}
