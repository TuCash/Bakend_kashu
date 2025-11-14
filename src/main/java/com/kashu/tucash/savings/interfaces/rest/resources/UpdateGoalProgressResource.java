package com.kashu.tucash.savings.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateGoalProgressResource(
        @NotNull(message = "El monto actual es requerido")
        @PositiveOrZero(message = "El monto debe ser cero o positivo")
        BigDecimal currentAmount
) {
}
