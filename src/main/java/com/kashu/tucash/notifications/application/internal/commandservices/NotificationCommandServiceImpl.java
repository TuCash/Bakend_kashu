package com.kashu.tucash.notifications.application.internal.commandservices;

import com.kashu.tucash.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.kashu.tucash.notifications.domain.model.aggregates.Notification;
import com.kashu.tucash.notifications.domain.model.commands.CreateNotificationCommand;
import com.kashu.tucash.notifications.domain.model.commands.DeleteNotificationCommand;
import com.kashu.tucash.notifications.domain.model.commands.MarkAsReadCommand;
import com.kashu.tucash.notifications.domain.services.NotificationCommandService;
import com.kashu.tucash.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationCommandServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Optional<Notification> handle(CreateNotificationCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        var notification = new Notification(command, user);
        var createdNotification = notificationRepository.save(notification);

        return Optional.of(createdNotification);
    }

    @Override
    @Transactional
    public Optional<Notification> handle(MarkAsReadCommand command) {
        var notification = notificationRepository.findById(command.notificationId())
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrada"));

        notification.markAsRead();
        var updatedNotification = notificationRepository.save(notification);

        return Optional.of(updatedNotification);
    }

    @Override
    @Transactional
    public void handle(DeleteNotificationCommand command) {
        var notification = notificationRepository.findById(command.notificationId())
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrada"));

        notificationRepository.delete(notification);
    }
}
