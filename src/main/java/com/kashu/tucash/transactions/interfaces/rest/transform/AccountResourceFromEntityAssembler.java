package com.kashu.tucash.transactions.interfaces.rest.transform;

import com.kashu.tucash.transactions.domain.model.aggregates.Account;
import com.kashu.tucash.transactions.interfaces.rest.resources.AccountResource;

public class AccountResourceFromEntityAssembler {
    public static AccountResource toResourceFromEntity(Account entity) {
        return new AccountResource(
                entity.getId(),
                entity.getName(),
                entity.getCurrency(),
                entity.getBalance(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
