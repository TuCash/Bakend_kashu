package com.kashu.tucash.transactions.interfaces.rest.transform;

import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;
import com.kashu.tucash.transactions.interfaces.rest.resources.TransactionResource;

public class TransactionResourceFromEntityAssembler {
    public static TransactionResource toResourceFromEntity(Transaction entity) {
        return new TransactionResource(
                entity.getId(),
                entity.getAccount().getId(),
                entity.getAccount().getName(),
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getIcon(),
                entity.getType().name(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getTransactionDate(),
                entity.getCreatedAt()
        );
    }
}
