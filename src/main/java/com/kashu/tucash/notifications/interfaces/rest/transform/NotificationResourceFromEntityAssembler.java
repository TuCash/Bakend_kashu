package com.kashu.tucash.notifications.interfaces.rest.transform;

import com.kashu.tucash.notifications.domain.model.aggregates.Notification;
import com.kashu.tucash.notifications.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getId(),
                entity.getUser().getId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType().name(),
                entity.getIsRead(),
                entity.getReadAt(),
                entity.getRelatedEntityType(),
                entity.getRelatedEntityId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
