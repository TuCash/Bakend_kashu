package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.commands.UpdateUserCommand;
import com.kashu.tucash.iam.interfaces.rest.resources.UpdateUserResource;

public class UpdateUserCommandFromResourceAssembler {
    public static UpdateUserCommand toCommandFromResource(Long userId, UpdateUserResource resource) {
        return new UpdateUserCommand(
                userId,
                resource.displayName(),
                resource.photoUrl(),
                resource.currency(),
                resource.theme(),
                resource.locale()
        );
    }
}
