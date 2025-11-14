package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.commands.SignInCommand;
import com.kashu.tucash.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(
                resource.email(),
                resource.password()
        );
    }
}
