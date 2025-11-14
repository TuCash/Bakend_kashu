package com.kashu.tucash.iam.interfaces.rest.transform;

import com.kashu.tucash.iam.domain.model.commands.RequestPasswordResetCommand;
import com.kashu.tucash.iam.interfaces.rest.resources.RequestPasswordResetResource;

public class RequestPasswordResetCommandFromResourceAssembler {
    public static RequestPasswordResetCommand toCommandFromResource(RequestPasswordResetResource resource) {
        return new RequestPasswordResetCommand(resource.email());
    }
}
