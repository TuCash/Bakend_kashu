package com.kashu.tucash.transactions.application.internal.queryservices;

import com.kashu.tucash.transactions.domain.model.aggregates.Account;
import com.kashu.tucash.transactions.domain.model.queries.GetAccountByIdQuery;
import com.kashu.tucash.transactions.domain.model.queries.GetAllAccountsByUserIdQuery;
import com.kashu.tucash.transactions.domain.services.AccountQueryService;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepository accountRepository;

    public AccountQueryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> handle(GetAllAccountsByUserIdQuery query) {
        return accountRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<Account> handle(GetAccountByIdQuery query) {
        return accountRepository.findById(query.accountId());
    }
}
