package com.kashu.tucash.transactions.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.transactions.domain.model.aggregates.Account;
import com.kashu.tucash.transactions.domain.model.commands.CreateAccountCommand;
import com.kashu.tucash.transactions.domain.services.AccountCommandService;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountCommandServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Account> handle(CreateAccountCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var account = new Account(command, user);
        var createdAccount = accountRepository.save(account);

        return Optional.of(createdAccount);
    }
}
