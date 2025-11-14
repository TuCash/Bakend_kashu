package com.kashu.tucash.sharedexpenses.domain.services;

import com.kashu.tucash.sharedexpenses.domain.model.aggregates.SharedExpense;
import com.kashu.tucash.sharedexpenses.domain.model.entities.SharedExpenseParticipant;
import com.kashu.tucash.sharedexpenses.domain.model.queries.*;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface SharedExpenseQueryService {
    Page<SharedExpense> handle(GetAllSharedExpensesByUserIdQuery query);
    Optional<SharedExpense> handle(GetSharedExpenseByIdQuery query);
    Page<SharedExpense> handle(GetSharedExpensesByStatusQuery query);
    Page<SharedExpenseParticipant> handle(GetPendingPaymentsByUserIdQuery query);
}
