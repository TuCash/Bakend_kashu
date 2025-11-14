package com.kashu.tucash.reminders.domain.model.queries;

import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;

public record GetAllRemindersByUserIdQuery(
        Long userId,
        ReminderType type,
        Boolean isActive
) {
}
