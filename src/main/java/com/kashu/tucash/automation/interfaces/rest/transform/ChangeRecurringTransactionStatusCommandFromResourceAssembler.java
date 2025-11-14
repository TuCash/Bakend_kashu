package com.kashu.tucash.automation.interfaces.rest.transform;

import com.kashu.tucash.automation.domain.model.commands.ChangeRecurringTransactionStatusCommand;
import com.kashu.tucash.automation.interfaces.rest.resources.ChangeRecurringTransactionStatusResource;

public class ChangeRecurringTransactionStatusCommandFromResourceAssembler {

    private ChangeRecurringTransactionStatusCommandFromResourceAssembler() {
    }

    public static ChangeRecurringTransactionStatusCommand toCommandFromResource(Long recurringTransactionId,
                                                                                ChangeRecurringTransactionStatusResource resource) {
        return new ChangeRecurringTransactionStatusCommand(recurringTransactionId, resource.active());
    }
}
