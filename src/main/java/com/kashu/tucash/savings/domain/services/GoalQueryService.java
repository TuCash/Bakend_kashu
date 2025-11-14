package com.kashu.tucash.savings.domain.services;

import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface GoalQueryService {
    List<Goal> handle(GetAllGoalsByUserIdQuery query);
    Optional<Goal> handle(GetGoalByIdQuery query);
}
