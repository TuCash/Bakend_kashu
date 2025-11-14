package com.kashu.tucash.sharedexpenses.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.notifications.domain.model.commands.CreateNotificationCommand;
import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;
import com.kashu.tucash.notifications.domain.services.NotificationCommandService;
import com.kashu.tucash.reminders.domain.model.commands.CreateReminderCommand;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderFrequency;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;
import com.kashu.tucash.reminders.domain.services.ReminderCommandService;
import com.kashu.tucash.sharedexpenses.domain.model.aggregates.SharedExpense;
import com.kashu.tucash.sharedexpenses.domain.model.commands.*;
import com.kashu.tucash.sharedexpenses.domain.model.entities.SharedExpenseParticipant;
import com.kashu.tucash.sharedexpenses.domain.services.SharedExpenseCommandService;
import com.kashu.tucash.sharedexpenses.infrastructure.persistence.jpa.repositories.SharedExpenseParticipantRepository;
import com.kashu.tucash.sharedexpenses.infrastructure.persistence.jpa.repositories.SharedExpenseRepository;
import com.kashu.tucash.transactions.infrastructure.persistence.jpa.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class SharedExpenseCommandServiceImpl implements SharedExpenseCommandService {

    private final SharedExpenseRepository sharedExpenseRepository;
    private final SharedExpenseParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationCommandService notificationCommandService;
    private final ReminderCommandService reminderCommandService;

    public SharedExpenseCommandServiceImpl(
            SharedExpenseRepository sharedExpenseRepository,
            SharedExpenseParticipantRepository participantRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            NotificationCommandService notificationCommandService,
            ReminderCommandService reminderCommandService) {
        this.sharedExpenseRepository = sharedExpenseRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.notificationCommandService = notificationCommandService;
        this.reminderCommandService = reminderCommandService;
    }

    @Override
    @Transactional
    public Optional<SharedExpense> handle(CreateSharedExpenseCommand command) {
        var creator = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        var sharedExpense = new SharedExpense(
                creator,
                command.title(),
                command.description(),
                command.totalAmount(),
                command.currency(),
                category,
                command.expenseDate(),
                command.splitMethod()
        );

        var savedExpense = sharedExpenseRepository.save(sharedExpense);

        // Add participants
        if (command.participants() != null && !command.participants().isEmpty()) {
            for (var participantDTO : command.participants()) {
                var participantUser = userRepository.findById(participantDTO.userId())
                        .orElseThrow(() -> new IllegalArgumentException("Participant user not found: " + participantDTO.userId()));

                var participant = new SharedExpenseParticipant(
                        savedExpense,
                        participantUser,
                        participantDTO.amountOwed()
                );
                savedExpense.addParticipant(participant);
            }
            sharedExpenseRepository.save(savedExpense);
            savedExpense.getParticipants().forEach(participant ->
                    dispatchParticipantReminder(savedExpense, participant));
        }

        return Optional.of(savedExpense);
    }

    @Override
    @Transactional
    public Optional<SharedExpense> handle(UpdateSharedExpenseCommand command) {
        var sharedExpense = sharedExpenseRepository.findById(command.sharedExpenseId())
                .orElseThrow(() -> new IllegalArgumentException("Shared expense not found"));

        if (command.title() != null) {
            sharedExpense.setTitle(command.title());
        }
        if (command.description() != null) {
            sharedExpense.setDescription(command.description());
        }
        if (command.totalAmount() != null) {
            sharedExpense.setTotalAmount(command.totalAmount());
        }
        if (command.currency() != null) {
            sharedExpense.setCurrency(command.currency());
        }
        if (command.categoryId() != null) {
            var category = categoryRepository.findById(command.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            sharedExpense.setCategory(category);
        }
        if (command.expenseDate() != null) {
            sharedExpense.setExpenseDate(command.expenseDate());
        }
        if (command.splitMethod() != null) {
            sharedExpense.setSplitMethod(command.splitMethod());
        }

        var updatedExpense = sharedExpenseRepository.save(sharedExpense);
        return Optional.of(updatedExpense);
    }

    @Override
    @Transactional
    public Optional<SharedExpense> handle(SettleSharedExpenseCommand command) {
        var sharedExpense = sharedExpenseRepository.findById(command.sharedExpenseId())
                .orElseThrow(() -> new IllegalArgumentException("Shared expense not found"));

        sharedExpense.settle();
        var settledExpense = sharedExpenseRepository.save(sharedExpense);
        return Optional.of(settledExpense);
    }

    @Override
    @Transactional
    public Optional<SharedExpenseParticipant> handle(AddParticipantCommand command) {
        var sharedExpense = sharedExpenseRepository.findById(command.sharedExpenseId())
                .orElseThrow(() -> new IllegalArgumentException("Shared expense not found"));

        var participantUser = userRepository.findById(command.participantUserId())
                .orElseThrow(() -> new IllegalArgumentException("Participant user not found"));

        var participant = new SharedExpenseParticipant(
                sharedExpense,
                participantUser,
                command.amountOwed()
        );

        sharedExpense.addParticipant(participant);
        sharedExpenseRepository.save(sharedExpense);

        var savedParticipant = participantRepository.save(participant);
        dispatchParticipantReminder(sharedExpense, savedParticipant);
        return Optional.of(savedParticipant);
    }

    @Override
    @Transactional
    public Optional<SharedExpenseParticipant> handle(MarkParticipantAsPaidCommand command) {
        var participant = participantRepository.findById(command.participantId())
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        participant.markAsPaid();
        var updatedParticipant = participantRepository.save(participant);

        var expense = participant.getSharedExpense();
        notificationCommandService.handle(new CreateNotificationCommand(
                expense.getCreator().getId(),
                "Pago recibido",
                "El participante " + participant.getParticipant().getDisplayName() + " pago su parte en "
                        + expense.getTitle(),
                NotificationType.SUCCESS,
                "SharedExpense",
                expense.getId()
        ));
        return Optional.of(updatedParticipant);
    }

    @Override
    @Transactional
    public void handle(DeleteSharedExpenseCommand command) {
        var sharedExpense = sharedExpenseRepository.findById(command.sharedExpenseId())
                .orElseThrow(() -> new IllegalArgumentException("Shared expense not found"));

        sharedExpenseRepository.delete(sharedExpense);
    }

    private void dispatchParticipantReminder(SharedExpense expense, SharedExpenseParticipant participant) {
        notificationCommandService.handle(new CreateNotificationCommand(
                participant.getParticipant().getId(),
                "Tienes un gasto compartido pendiente",
                "Debes " + participant.getAmountOwed() + " en " + expense.getTitle(),
                NotificationType.REMINDER,
                "SharedExpense",
                expense.getId()
        ));

        LocalDate reminderDate = expense.getExpenseDate() != null ? expense.getExpenseDate() : LocalDate.now();
        LocalDateTime reminderTime = LocalDateTime.of(reminderDate, LocalTime.of(9, 0));

        reminderCommandService.handle(new CreateReminderCommand(
                participant.getParticipant().getId(),
                "Paga tu parte de " + expense.getTitle(),
                "Recuerda cancelar " + participant.getAmountOwed() + " a " + expense.getCreator().getDisplayName(),
                ReminderType.PAYMENT,
                reminderDate,
                reminderTime,
                ReminderFrequency.ONCE,
                expense.getId()
        ));
    }
}
