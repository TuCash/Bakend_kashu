package com.kashu.tucash.reminders.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.reminders.domain.model.aggregates.Reminder;
import com.kashu.tucash.reminders.domain.model.commands.CreateReminderCommand;
import com.kashu.tucash.reminders.domain.model.commands.DeleteReminderCommand;
import com.kashu.tucash.reminders.domain.model.commands.UpdateReminderCommand;
import com.kashu.tucash.reminders.domain.services.ReminderCommandService;
import com.kashu.tucash.reminders.infrastructure.persistence.jpa.repositories.ReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ReminderCommandServiceImpl implements ReminderCommandService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;

    public ReminderCommandServiceImpl(
            ReminderRepository reminderRepository,
            UserRepository userRepository) {
        this.reminderRepository = reminderRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Optional<Reminder> handle(CreateReminderCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var reminder = new Reminder(command, user);
        var createdReminder = reminderRepository.save(reminder);

        return Optional.of(createdReminder);
    }

    @Override
    @Transactional
    public Optional<Reminder> handle(UpdateReminderCommand command) {
        var reminder = reminderRepository.findById(command.reminderId())
                .orElseThrow(() -> new IllegalArgumentException("Recordatorio no encontrado"));

        reminder.update(command);
        var updatedReminder = reminderRepository.save(reminder);

        return Optional.of(updatedReminder);
    }

    @Override
    @Transactional
    public void handle(DeleteReminderCommand command) {
        var reminder = reminderRepository.findById(command.reminderId())
                .orElseThrow(() -> new IllegalArgumentException("Recordatorio no encontrado"));

        reminderRepository.delete(reminder);
    }
}
