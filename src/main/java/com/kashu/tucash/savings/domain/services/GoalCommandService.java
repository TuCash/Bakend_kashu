package com.kashu.tucash.savings.domain.services;

import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.domain.model.commands.*;

import java.util.Optional;

public interface GoalCommandService {
    Optional<Goal> handle(CreateGoalCommand command);
    Optional<Goal> handle(UpdateGoalCommand command);
    Optional<Goal> handle(UpdateGoalProgressCommand command);
    Optional<Goal> handle(CelebrateGoalCommand command);
    void handle(DeleteGoalCommand command);
}
