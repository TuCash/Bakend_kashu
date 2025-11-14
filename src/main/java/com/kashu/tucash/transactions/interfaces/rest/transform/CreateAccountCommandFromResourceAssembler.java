package com.kashu.tucash.transactions.interfaces.rest.transform;

import com.kashu.tucash.transactions.domain.model.commands.CreateAccountCommand;
import com.kashu.tucash.transactions.interfaces.rest.resources.CreateAccountResource;

public class CreateAccountCommandFromResourceAssembler {
    public static CreateAccountCommand toCommandFromResource(Long userId, CreateAccountResource resource) {
        return new CreateAccountCommand(
                userId,
                resource.name(),
                resource.currency()
        );
    }
}
