package com.kashu.tucash.transactions.interfaces.rest;

import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import com.kashu.tucash.transactions.domain.model.commands.DeleteTransactionCommand;
import com.kashu.tucash.transactions.domain.model.queries.GetAllTransactionsByUserIdQuery;
import com.kashu.tucash.transactions.domain.model.queries.GetTransactionByIdQuery;
import com.kashu.tucash.transactions.domain.model.valueobjects.TransactionType;
import com.kashu.tucash.transactions.domain.services.TransactionCommandService;
import com.kashu.tucash.transactions.domain.services.TransactionQueryService;
import com.kashu.tucash.transactions.interfaces.rest.resources.CreateTransactionResource;
import com.kashu.tucash.transactions.interfaces.rest.resources.TransactionResource;
import com.kashu.tucash.transactions.interfaces.rest.resources.UpdateTransactionResource;
import com.kashu.tucash.transactions.interfaces.rest.transform.CreateTransactionCommandFromResourceAssembler;
import com.kashu.tucash.transactions.interfaces.rest.transform.TransactionResourceFromEntityAssembler;
import com.kashu.tucash.transactions.interfaces.rest.transform.UpdateTransactionCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/transactions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Transactions", description = "Endpoints de gestión de transacciones")
@SecurityRequirement(name = "bearerAuth")
public class TransactionsController {

    private final TransactionCommandService transactionCommandService;
    private final TransactionQueryService transactionQueryService;
    private final AuthenticationHelper authenticationHelper;

    public TransactionsController(TransactionCommandService transactionCommandService,
                                  TransactionQueryService transactionQueryService,
                                  AuthenticationHelper authenticationHelper) {
        this.transactionCommandService = transactionCommandService;
        this.transactionQueryService = transactionQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Obtener transacciones con filtros",
            description = "Devuelve transacciones del usuario con filtros opcionales (tipo, categoría, fechas) y paginación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacciones encontradas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<Page<TransactionResource>> getAllTransactions(
            @Parameter(description = "Tipo de transacción: INCOME, EXPENSE, TRANSFER")
            @RequestParam(required = false) String type,

            @Parameter(description = "ID de categoría")
            @RequestParam(required = false) Long categoryId,

            @Parameter(description = "Fecha desde (formato: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "Fecha hasta (formato: yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

            @Parameter(description = "Número de página (empieza en 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "20") int size,

            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        TransactionType transactionType = type != null ? TransactionType.valueOf(type.toUpperCase()) : null;
        Pageable pageable = PageRequest.of(page, size);

        var query = new GetAllTransactionsByUserIdQuery(
                userId,
                transactionType,
                categoryId,
                fromDate,
                toDate,
                pageable
        );

        var transactions = transactionQueryService.handle(query);
        var resources = transactions.map(TransactionResourceFromEntityAssembler::toResourceFromEntity);

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Obtener transacción por ID",
            description = "Devuelve una transacción específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción encontrada"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResource> getTransactionById(@PathVariable Long id) {
        var query = new GetTransactionByIdQuery(id);
        var transaction = transactionQueryService.handle(query);

        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Crear transacción",
            description = "Crea una nueva transacción (ingreso, gasto o transferencia)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transacción creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<TransactionResource> createTransaction(
            @Valid @RequestBody CreateTransactionResource resource,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        var command = CreateTransactionCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var transaction = transactionCommandService.handle(command);

        if (transaction.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
        return new ResponseEntity<>(transactionResource, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar transacción",
            description = "Actualiza una transacción existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción actualizada"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResource> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTransactionResource resource) {

        var command = UpdateTransactionCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var transaction = transactionCommandService.handle(command);

        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
        return ResponseEntity.ok(transactionResource);
    }

    @Operation(
            summary = "Eliminar transacción",
            description = "Elimina una transacción existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transacción eliminada"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        var command = new DeleteTransactionCommand(id);
        transactionCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return authenticationHelper.getUserIdFromAuthentication(authentication);
    }
}
