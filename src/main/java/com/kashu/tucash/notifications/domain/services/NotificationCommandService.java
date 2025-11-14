package com.kashu.tucash.notifications.domain.services;

import com.kashu.tucash.notifications.domain.model.aggregates.Notification;
import com.kashu.tucash.notifications.domain.model.commands.CreateNotificationCommand;
import com.kashu.tucash.notifications.domain.model.commands.DeleteNotificationCommand;
import com.kashu.tucash.notifications.domain.model.commands.MarkAsReadCommand;

import java.util.Optional;

public interface NotificationCommandService {
    Optional<Notification> handle(CreateNotificationCommand command);
    Optional<Notification> handle(MarkAsReadCommand command);
    void handle(DeleteNotificationCommand command);
}
