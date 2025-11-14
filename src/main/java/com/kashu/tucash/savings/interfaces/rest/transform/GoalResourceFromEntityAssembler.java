package com.kashu.tucash.savings.interfaces.rest.transform;

import com.kashu.tucash.savings.domain.model.aggregates.Goal;
import com.kashu.tucash.savings.interfaces.rest.resources.GoalResource;

public class GoalResourceFromEntityAssembler {
    public static GoalResource toResourceFromEntity(Goal entity) {
        return new GoalResource(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getTargetAmount(),
                entity.getCurrentAmount(),
                entity.getProgressPercentage(),
                entity.getDeadline(),
                entity.getStatus().name(),
                entity.getCelebratedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
