package com.kashu.tucash.sharedexpenses.interfaces.rest.transform;

import com.kashu.tucash.sharedexpenses.domain.model.aggregates.SharedExpense;
import com.kashu.tucash.sharedexpenses.interfaces.rest.resources.SharedExpenseResource;

import java.util.stream.Collectors;

public class SharedExpenseResourceFromEntityAssembler {

    public static SharedExpenseResource toResourceFromEntity(SharedExpense entity) {
        var participants = entity.getParticipants().stream()
                .map(ParticipantResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return new SharedExpenseResource(
                entity.getId(),
                entity.getCreator().getId(),
                entity.getCreator().getDisplayName(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getTotalAmount(),
                entity.getCurrency(),
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getCategory().getIcon(),
                entity.getExpenseDate(),
                entity.getStatus().name(),
                entity.getSplitMethod().name(),
                entity.getSettledAt(),
                participants,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
