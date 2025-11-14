package com.kashu.tucash.sharedexpenses.interfaces.rest.transform;

import com.kashu.tucash.sharedexpenses.domain.model.entities.SharedExpenseParticipant;
import com.kashu.tucash.sharedexpenses.interfaces.rest.resources.ParticipantResource;

public class ParticipantResourceFromEntityAssembler {

    public static ParticipantResource toResourceFromEntity(SharedExpenseParticipant entity) {
        return new ParticipantResource(
                entity.getId(),
                entity.getSharedExpense().getId(),
                entity.getParticipant().getId(),
                entity.getParticipant().getDisplayName(),
                entity.getParticipant().getEmail(),
                entity.getAmountOwed(),
                entity.getAmountPaid(),
                entity.getIsPaid(),
                entity.getPaidAt()
        );
    }
}
