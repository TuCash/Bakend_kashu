package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.commands.ChangePasswordCommand;
import com.kashu.tucash.iam.interfaces.rest.resources.ChangePasswordResource;

public class ChangePasswordCommandFromResourceAssembler {
    public static ChangePasswordCommand toCommandFromResource(Long userId, ChangePasswordResource resource) {
        return new ChangePasswordCommand(
                userId,
                resource.currentPassword(),
                resource.newPassword()
        );
    }
}
