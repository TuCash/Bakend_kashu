package com.kashu.tucash.savings.domain.model.commands;

import java.math.BigDecimal;

/**
 * Command to add a contribution to a savings goal.
 * This will create an EXPENSE transaction from the specified account
 * and add the amount to the goal's currentAmount.
 */
public record ContributeToGoalCommand(
        Long userId,
        Long goalId,
        Long accountId,
        BigDecimal amount,
        String description
) {
}
