package com.kashu.tucash.reminders.domain.model.aggregates;

import com.kashu.tucash.iam.domain.model.aggregates.User;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderFrequency;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;
import com.kashu.tucash.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
public class Reminder extends AuditableAbstractAggregateRoot<Reminder> {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    @Setter
    private String title;

    @Setter
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private ReminderType type;

    @NotNull
    @Column(nullable = false)
    @Setter
    private LocalDate reminderDate;

    @Column(nullable = false)
    @Setter
    private LocalDateTime reminderTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private ReminderFrequency frequency;

    @Column(nullable = false)
    @Setter
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean isNotified = false;

    @Setter
    private LocalDateTime lastNotificationAt;

    // Reference to related entity
    @Setter
    private Long relatedEntityId;

    protected Reminder() {
    }

    public Reminder(User user, String title, String description, ReminderType type,
                   LocalDate reminderDate, LocalDateTime reminderTime, ReminderFrequency frequency) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
        this.frequency = frequency;
        this.isActive = true;
        this.isNotified = false;
    }

    public Reminder(com.kashu.tucash.reminders.domain.model.commands.CreateReminderCommand command, User user) {
        this.user = user;
        this.title = command.title();
        this.description = command.description();
        this.type = command.type();
        this.reminderDate = command.reminderDate();
        this.reminderTime = command.reminderTime();
        this.frequency = command.frequency();
        this.relatedEntityId = command.relatedEntityId();
        this.isActive = true;
        this.isNotified = false;
    }

    public void update(com.kashu.tucash.reminders.domain.model.commands.UpdateReminderCommand command) {
        if (command.title() != null) {
            this.title = command.title();
        }
        if (command.description() != null) {
            this.description = command.description();
        }
        if (command.type() != null) {
            this.type = command.type();
        }
        if (command.reminderDate() != null) {
            this.reminderDate = command.reminderDate();
        }
        if (command.reminderTime() != null) {
            this.reminderTime = command.reminderTime();
        }
        if (command.frequency() != null) {
            this.frequency = command.frequency();
        }
        if (command.isActive() != null) {
            this.isActive = command.isActive();
        }
        if (command.relatedEntityId() != null) {
            this.relatedEntityId = command.relatedEntityId();
        }
    }

    public void markAsNotified() {
        this.isNotified = true;
        this.lastNotificationAt = LocalDateTime.now();
    }

    public void resetNotification() {
        this.isNotified = false;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}
