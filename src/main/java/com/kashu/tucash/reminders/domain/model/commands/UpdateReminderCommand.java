package com.kashu.tucash.reminders.domain.model.commands;

import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderFrequency;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateReminderCommand(
        Long reminderId,
        String title,
        String description,
        ReminderType type,
        LocalDate reminderDate,
        LocalDateTime reminderTime,
        ReminderFrequency frequency,
        Boolean isActive,
        Long relatedEntityId
) {
}
