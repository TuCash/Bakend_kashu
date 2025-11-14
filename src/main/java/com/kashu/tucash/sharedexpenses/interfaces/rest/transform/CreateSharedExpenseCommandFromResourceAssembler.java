package com.kashu.tucash.sharedexpenses.interfaces.rest.transform;

import com.kashu.tucash.sharedexpenses.domain.model.commands.CreateSharedExpenseCommand;
import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SplitMethod;
import com.kashu.tucash.sharedexpenses.interfaces.rest.resources.CreateSharedExpenseResource;

import java.util.stream.Collectors;

public class CreateSharedExpenseCommandFromResourceAssembler {

    public static CreateSharedExpenseCommand toCommandFromResource(
            Long userId,
            CreateSharedExpenseResource resource) {

        var participants = resource.participants().stream()
                .map(p -> new CreateSharedExpenseCommand.ParticipantDTO(
                        p.userId(),
                        p.amountOwed()
                ))
                .collect(Collectors.toList());

        return new CreateSharedExpenseCommand(
                userId,
                resource.title(),
                resource.description(),
                resource.totalAmount(),
                resource.currency() != null ? resource.currency() : "PEN",
                resource.categoryId(),
                resource.expenseDate(),
                SplitMethod.valueOf(resource.splitMethod().toUpperCase()),
                participants
        );
    }
}
