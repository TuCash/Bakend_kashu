package com.kashu.tucash.transactions.domain.model.commands;

public record CreateAccountCommand(
        Long userId,
        String name,
        String currency
) {
}
