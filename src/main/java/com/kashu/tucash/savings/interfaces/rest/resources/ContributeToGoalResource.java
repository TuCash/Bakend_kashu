package com.kashu.tucash.savings.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ContributeToGoalResource(
        @NotNull(message = "La cuenta es requerida")
        Long accountId,

        @NotNull(message = "El monto es requerido")
        @Positive(message = "El monto debe ser positivo")
        BigDecimal amount,

        String description
) {
}
