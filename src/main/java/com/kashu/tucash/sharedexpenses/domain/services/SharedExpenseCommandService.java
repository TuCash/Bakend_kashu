package com.kashu.tucash.sharedexpenses.domain.services;

import com.kashu.tucash.sharedexpenses.domain.model.aggregates.SharedExpense;
import com.kashu.tucash.sharedexpenses.domain.model.commands.*;
import com.kashu.tucash.sharedexpenses.domain.model.entities.SharedExpenseParticipant;

import java.util.Optional;

public interface SharedExpenseCommandService {
    Optional<SharedExpense> handle(CreateSharedExpenseCommand command);
    Optional<SharedExpense> handle(UpdateSharedExpenseCommand command);
    Optional<SharedExpense> handle(SettleSharedExpenseCommand command);
    Optional<SharedExpenseParticipant> handle(AddParticipantCommand command);
    Optional<SharedExpenseParticipant> handle(MarkParticipantAsPaidCommand command);
    void handle(DeleteSharedExpenseCommand command);
}
