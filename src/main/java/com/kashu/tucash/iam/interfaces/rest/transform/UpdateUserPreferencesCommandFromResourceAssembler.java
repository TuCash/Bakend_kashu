package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.commands.UpdateUserPreferencesCommand;
import com.kashu.tucash.iam.interfaces.rest.resources.UpdateUserPreferencesResource;

public class UpdateUserPreferencesCommandFromResourceAssembler {
    public static UpdateUserPreferencesCommand toCommandFromResource(Long userId, UpdateUserPreferencesResource resource) {
        return new UpdateUserPreferencesCommand(
                userId,
                resource.currency(),
                resource.theme(),
                resource.locale(),
                resource.notificationsEnabled(),
                resource.emailNotifications(),
                resource.pushNotifications()
        );
    }
}
