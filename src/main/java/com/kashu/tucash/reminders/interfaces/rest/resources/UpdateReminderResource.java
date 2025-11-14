package com.kashu.tucash.reminders.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateReminderResource(
        String title,
        String description,
        String type,
        LocalDate reminderDate,
        LocalDateTime reminderTime,
        String frequency,
        Boolean isActive,
        Long relatedEntityId
) {
}
