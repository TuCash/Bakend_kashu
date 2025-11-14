package com.kashu.tucash.reminders.domain.services;

import com.kashu.tucash.reminders.domain.model.aggregates.Reminder;
import com.kashu.tucash.reminders.domain.model.queries.GetActiveRemindersQuery;
import com.kashu.tucash.reminders.domain.model.queries.GetAllRemindersByUserIdQuery;
import com.kashu.tucash.reminders.domain.model.queries.GetReminderByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ReminderQueryService {
    Optional<Reminder> handle(GetReminderByIdQuery query);
    List<Reminder> handle(GetAllRemindersByUserIdQuery query);
    List<Reminder> handle(GetActiveRemindersQuery query);
}
