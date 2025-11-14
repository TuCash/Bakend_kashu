package com.kashu.tucash.savings.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBudgetResource(
        @NotNull(message = "El ID de categoría es requerido")
        Long categoryId,

        @NotNull(message = "El monto límite es requerido")
        @Positive(message = "El monto debe ser positivo")
        BigDecimal limitAmount,

        @NotNull(message = "El período es requerido")
        String period,

        @NotNull(message = "La fecha de inicio es requerida")
        LocalDate startDate,

        @NotNull(message = "La fecha de fin es requerida")
        LocalDate endDate
) {
}
