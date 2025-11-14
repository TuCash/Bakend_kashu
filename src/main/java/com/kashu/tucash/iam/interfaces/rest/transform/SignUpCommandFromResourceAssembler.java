package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.commands.SignUpCommand;
import com.kashu.tucash.iam.interfaces.rest.resources.SignUpResource;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        return new SignUpCommand(
                resource.email(),
                resource.password(),
                resource.displayName()
        );
    }
}
