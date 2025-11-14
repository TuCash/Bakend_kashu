package com.kashu.tucash.reminders.domain.services;

import com.kashu.tucash.reminders.domain.model.aggregates.Reminder;
import com.kashu.tucash.reminders.domain.model.commands.CreateReminderCommand;
import com.kashu.tucash.reminders.domain.model.commands.DeleteReminderCommand;
import com.kashu.tucash.reminders.domain.model.commands.UpdateReminderCommand;

import java.util.Optional;

public interface ReminderCommandService {
    Optional<Reminder> handle(CreateReminderCommand command);
    Optional<Reminder> handle(UpdateReminderCommand command);
    void handle(DeleteReminderCommand command);
}
