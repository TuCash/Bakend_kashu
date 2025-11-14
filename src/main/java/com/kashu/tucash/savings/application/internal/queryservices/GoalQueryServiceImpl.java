package com.kashu.tucash.savings.application.internal.queryservices;

import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.domain.model.queries.*;
import com.kashu.tucash.savings.domain.services.GoalQueryService;
import com.kashu.tucash.savings.infrastructure.persistence.jpa.repositories.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalQueryServiceImpl implements GoalQueryService {

    private final GoalRepository goalRepository;

    public GoalQueryServiceImpl(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    @Override
    public List<Goal> handle(GetAllGoalsByUserIdQuery query) {
        return goalRepository.findByUserId(query.userId());
    }

    @Override
    public Optional<Goal> handle(GetGoalByIdQuery query) {
        return goalRepository.findById(query.goalId());
    }
}
