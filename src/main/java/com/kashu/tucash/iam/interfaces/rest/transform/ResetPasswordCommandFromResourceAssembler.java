package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.commands.ResetPasswordCommand;
import com.kashu.tucash.iam.interfaces.rest.resources.ResetPasswordResource;

public class ResetPasswordCommandFromResourceAssembler {
    public static ResetPasswordCommand toCommandFromResource(ResetPasswordResource resource) {
        return new ResetPasswordCommand(
                resource.token(),
                resource.newPassword()
        );
    }
}
