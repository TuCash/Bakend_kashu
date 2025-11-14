package com.kashu.tucash.automation.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record ChangeRecurringTransactionStatusResource(
        @NotNull Boolean active
) {
}
