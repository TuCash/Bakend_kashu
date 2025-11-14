package com.kashu.tucash.savings.domain.services;

import com.kashu.tucash.savings.domain.model.aggregates.Budget;
import com.kashu.tucash.savings.domain.model.commands.*;

import java.util.Optional;

public interface BudgetCommandService {
    Optional<Budget> handle(CreateBudgetCommand command);
    void handle(DeleteBudgetCommand command);
}
