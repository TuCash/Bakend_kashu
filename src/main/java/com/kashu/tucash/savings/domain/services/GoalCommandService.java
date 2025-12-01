package com.kashu.tucash.savings.domain.services;

import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.domain.model.commands.*;
import com.kashu.tucash.transactions.domain.model.aggregates.Transaction;

import java.util.List;
import java.util.Optional;

public interface GoalCommandService {
    Optional<Goal> handle(CreateGoalCommand command);
    Optional<Goal> handle(UpdateGoalCommand command);
    Optional<Goal> handle(UpdateGoalProgressCommand command);
    Optional<Goal> handle(CelebrateGoalCommand command);
    void handle(DeleteGoalCommand command);

    // Contribuciones a metas
    Optional<Transaction> handle(ContributeToGoalCommand command);
    Optional<Transaction> handle(RevertGoalContributionCommand command);
    List<Transaction> getGoalContributions(Long goalId);
}
