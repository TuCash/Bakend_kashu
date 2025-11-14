package com.kashu.tucash.transactions.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionResource(
        @NotNull(message = "El ID de cuenta es requerido")
        Long accountId,

        @NotNull(message = "El ID de categoría es requerido")
        Long categoryId,

        @NotNull(message = "El tipo es requerido")
        String type,

        @NotNull(message = "El monto es requerido")
        @Positive(message = "El monto debe ser positivo")
        BigDecimal amount,

        String description,

        @NotNull(message = "La fecha es requerida")
        LocalDate transactionDate
) {
}
