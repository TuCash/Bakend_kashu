package com.kashu.tucash.reminders.interfaces.rest.transform;

import com.kashu.tucash.reminders.domain.model.aggregates.Reminder;
import com.kashu.tucash.reminders.interfaces.rest.resources.ReminderResource;

public class ReminderResourceFromEntityAssembler {
    public static ReminderResource toResourceFromEntity(Reminder entity) {
        return new ReminderResource(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getType().name(),
                entity.getReminderDate(),
                entity.getReminderTime(),
                entity.getFrequency().name(),
                entity.getIsActive(),
                entity.getIsNotified(),
                entity.getLastNotificationAt(),
                entity.getRelatedEntityId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
