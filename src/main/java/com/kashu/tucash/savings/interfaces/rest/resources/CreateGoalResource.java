package com.kashu.tucash.savings.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateGoalResource(
        @NotBlank(message = "El nombre es requerido")
        String name,

        String description,

        @NotNull(message = "El monto objetivo es requerido")
        @Positive(message = "El monto debe ser positivo")
        BigDecimal targetAmount,

        @NotNull(message = "La fecha límite es requerida")
        LocalDate deadline
) {
}
