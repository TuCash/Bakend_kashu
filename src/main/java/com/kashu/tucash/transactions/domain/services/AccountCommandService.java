package com.kashu.tucash.transactions.domain.services;

import com.kashu.tucash.transactions.domain.model.aggregates.Account;
import com.kashu.tucash.transactions.domain.model.commands.CreateAccountCommand;

import java.util.Optional;

public interface AccountCommandService {
    Optional<Account> handle(CreateAccountCommand command);
}
