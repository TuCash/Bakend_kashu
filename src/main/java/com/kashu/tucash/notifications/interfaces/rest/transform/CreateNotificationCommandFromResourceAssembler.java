package com.kashu.tucash.notifications.interfaces.rest.transform;

import com.kashu.tucash.notifications.domain.model.commands.CreateNotificationCommand;
import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;
import com.kashu.tucash.notifications.interfaces.rest.resources.CreateNotificationResource;

public class CreateNotificationCommandFromResourceAssembler {
    public static CreateNotificationCommand toCommandFromResource(CreateNotificationResource resource) {
        return new CreateNotificationCommand(
                resource.userId(),
                resource.title(),
                resource.message(),
                NotificationType.valueOf(resource.type().toUpperCase()),
                resource.relatedEntityType(),
                resource.relatedEntityId()
        );
    }
}
