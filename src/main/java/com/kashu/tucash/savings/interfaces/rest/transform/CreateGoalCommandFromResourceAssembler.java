package com.kashu.tucash.savings.interfaces.rest.transform;

import com.kashu.tucash.savings.domain.model.commands.CreateGoalCommand;
import com.kashu.tucash.savings.interfaces.rest.resources.CreateGoalResource;

public class CreateGoalCommandFromResourceAssembler {
    public static CreateGoalCommand toCommandFromResource(Long userId, CreateGoalResource resource) {
        return new CreateGoalCommand(
                userId,
                resource.name(),
                resource.description(),
                resource.targetAmount(),
                resource.deadline()
        );
    }
}
