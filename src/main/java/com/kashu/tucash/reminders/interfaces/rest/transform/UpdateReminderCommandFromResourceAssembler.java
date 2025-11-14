package com.kashu.tucash.reminders.interfaces.rest.transform;

import com.kashu.tucash.reminders.domain.model.commands.UpdateReminderCommand;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderFrequency;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;
import com.kashu.tucash.reminders.interfaces.rest.resources.UpdateReminderResource;

public class UpdateReminderCommandFromResourceAssembler {
    public static UpdateReminderCommand toCommandFromResource(Long reminderId, UpdateReminderResource resource) {
        ReminderType type = resource.type() != null 
                ? ReminderType.valueOf(resource.type().toUpperCase()) 
                : null;
        
        ReminderFrequency frequency = resource.frequency() != null 
                ? ReminderFrequency.valueOf(resource.frequency().toUpperCase()) 
                : null;

        return new UpdateReminderCommand(
                reminderId,
                resource.title(),
                resource.description(),
                type,
                resource.reminderDate(),
                resource.reminderTime(),
                frequency,
                resource.isActive(),
                resource.relatedEntityId()
        );
    }
}
