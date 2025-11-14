package com.kashu.tucash.automation.domain.services;

import com.kashu.tucash.automation.domain.model.aggregates.RecurringTransaction;
import com.kashu.tucash.automation.domain.model.commands.ChangeRecurringTransactionStatusCommand;
import com.kashu.tucash.automation.domain.model.commands.CreateRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.model.commands.DeleteRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.model.commands.UpdateRecurringTransactionCommand;

import java.util.Optional;

public interface RecurringTransactionCommandService {
    Optional<RecurringTransaction> handle(CreateRecurringTransactionCommand command);
    Optional<RecurringTransaction> handle(UpdateRecurringTransactionCommand command);
    Optional<RecurringTransaction> handle(ChangeRecurringTransactionStatusCommand command);
    void handle(DeleteRecurringTransactionCommand command);
}
