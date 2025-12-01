package com.kashu.tucash.savings.domain.model.commands;

/**
 * Command to revert a contribution from a savings goal.
 * This will create an INCOME transaction to return the money to the original account
 * and subtract the amount from the goal's currentAmount.
 */
public record RevertGoalContributionCommand(
        Long userId,
        Long goalId,
        Long transactionId
) {
}
