package com.kashu.tucash.automation.interfaces.rest.transform;

import com.kashu.tucash.automation.domain.model.aggregates.RecurringTransaction;
import com.kashu.tucash.automation.interfaces.rest.resources.RecurringTransactionResource;

public class RecurringTransactionResourceFromEntityAssembler {

    private RecurringTransactionResourceFromEntityAssembler() {
    }

    public static RecurringTransactionResource toResourceFromEntity(RecurringTransaction entity) {
        return new RecurringTransactionResource(
                entity.getId(),
                entity.getAccount().getId(),
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getType().name(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getFrequency().name(),
                entity.getNextExecutionDate(),
                entity.getActive()
        );
    }
}
