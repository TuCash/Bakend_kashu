package com.kashu.tucash.reminders.interfaces.rest.transform;

import com.kashu.tucash.reminders.domain.model.commands.CreateReminderCommand;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderFrequency;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;
import com.kashu.tucash.reminders.interfaces.rest.resources.CreateReminderResource;

public class CreateReminderCommandFromResourceAssembler {
    public static CreateReminderCommand toCommandFromResource(Long userId, CreateReminderResource resource) {
        return new CreateReminderCommand(
                userId,
                resource.title(),
                resource.description(),
                ReminderType.valueOf(resource.type().toUpperCase()),
                resource.reminderDate(),
                resource.reminderTime(),
                ReminderFrequency.valueOf(resource.frequency().toUpperCase()),
                resource.relatedEntityId()
        );
    }
}
