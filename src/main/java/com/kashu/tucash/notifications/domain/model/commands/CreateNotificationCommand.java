package com.kashu.tucash.notifications.domain.model.commands;

import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;

public record CreateNotificationCommand(
        Long userId,
        String title,
        String message,
        NotificationType type,
        String relatedEntityType,
        Long relatedEntityId
) {
}
