package com.kashu.tucash.transactions.domain.services;

import com.kashu.tucash.transactions.domain.model.aggregates.Account;
import com.kashu.tucash.transactions.domain.model.queries.GetAccountByIdQuery;
import com.kashu.tucash.transactions.domain.model.queries.GetAllAccountsByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface AccountQueryService {
    List<Account> handle(GetAllAccountsByUserIdQuery query);
    Optional<Account> handle(GetAccountByIdQuery query);
}
