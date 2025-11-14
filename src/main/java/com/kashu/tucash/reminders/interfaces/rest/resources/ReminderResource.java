package com.kashu.tucash.reminders.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public record ReminderResource(
        Long id,
        String title,
        String description,
        String type,
        LocalDate reminderDate,
        LocalDateTime reminderTime,
        String frequency,
        Boolean isActive,
        Boolean isNotified,
        LocalDateTime lastNotificationAt,
        Long relatedEntityId,
        Date createdAt,
        Date updatedAt
) {
}
