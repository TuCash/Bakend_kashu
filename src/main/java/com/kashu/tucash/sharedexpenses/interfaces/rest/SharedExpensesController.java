package com.kashu.tucash.sharedexpenses.interfaces.rest;

import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import com.kashu.tucash.sharedexpenses.domain.model.commands.*;
import com.kashu.tucash.sharedexpenses.domain.model.queries.*;
import com.kashu.tucash.sharedexpenses.domain.model.valueobjects.SharedExpenseStatus;
import com.kashu.tucash.sharedexpenses.domain.services.SharedExpenseCommandService;
import com.kashu.tucash.sharedexpenses.domain.services.SharedExpenseQueryService;
import com.kashu.tucash.sharedexpenses.interfaces.rest.resources.*;
import com.kashu.tucash.sharedexpenses.interfaces.rest.transform.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/shared-expenses", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Shared Expenses", description = "Endpoints for managing shared expenses and bill splitting")
@SecurityRequirement(name = "bearerAuth")
public class SharedExpensesController {

    private final SharedExpenseCommandService commandService;
    private final SharedExpenseQueryService queryService;
    private final AuthenticationHelper authenticationHelper;

    public SharedExpensesController(
            SharedExpenseCommandService commandService,
            SharedExpenseQueryService queryService,
            AuthenticationHelper authenticationHelper) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Get all shared expenses for user",
            description = "Returns all shared expenses where the user is creator or participant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shared expenses found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<SharedExpenseResource>> getAllSharedExpenses(
            @Parameter(description = "Page number (starts at 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(page, size);

        var query = new GetAllSharedExpensesByUserIdQuery(userId, pageable);
        var expenses = queryService.handle(query);
        var resources = expenses.map(SharedExpenseResourceFromEntityAssembler::toResourceFromEntity);

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Get shared expense by ID",
            description = "Returns a specific shared expense by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shared expense found"),
            @ApiResponse(responseCode = "404", description = "Shared expense not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SharedExpenseResource> getSharedExpenseById(@PathVariable Long id) {
        var query = new GetSharedExpenseByIdQuery(id);
        var expense = queryService.handle(query);

        if (expense.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = SharedExpenseResourceFromEntityAssembler.toResourceFromEntity(expense.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Get pending payments for user",
            description = "Returns all pending payments where the user owes money")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending payments found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/pending-payments")
    public ResponseEntity<Page<ParticipantResource>> getPendingPayments(
            @Parameter(description = "Page number (starts at 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);
        Pageable pageable = PageRequest.of(page, size);

        var query = new GetPendingPaymentsByUserIdQuery(userId, pageable);
        var participants = queryService.handle(query);
        var resources = participants.map(ParticipantResourceFromEntityAssembler::toResourceFromEntity);

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Create shared expense",
            description = "Creates a new shared expense with participants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Shared expense created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<SharedExpenseResource> createSharedExpense(
            @Valid @RequestBody CreateSharedExpenseResource resource,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        var command = CreateSharedExpenseCommandFromResourceAssembler
                .toCommandFromResource(userId, resource);
        var expense = commandService.handle(command);

        if (expense.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var expenseResource = SharedExpenseResourceFromEntityAssembler
                .toResourceFromEntity(expense.get());
        return new ResponseEntity<>(expenseResource, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Update shared expense",
            description = "Updates an existing shared expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shared expense updated"),
            @ApiResponse(responseCode = "404", description = "Shared expense not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SharedExpenseResource> updateSharedExpense(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSharedExpenseResource resource) {

        var command = UpdateSharedExpenseCommandFromResourceAssembler
                .toCommandFromResource(id, resource);
        var expense = commandService.handle(command);

        if (expense.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var expenseResource = SharedExpenseResourceFromEntityAssembler
                .toResourceFromEntity(expense.get());
        return ResponseEntity.ok(expenseResource);
    }

    @Operation(
            summary = "Settle shared expense",
            description = "Marks a shared expense as completed/settled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shared expense settled"),
            @ApiResponse(responseCode = "404", description = "Shared expense not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/{id}/settle")
    public ResponseEntity<SharedExpenseResource> settleSharedExpense(@PathVariable Long id) {
        var command = new SettleSharedExpenseCommand(id);
        var expense = commandService.handle(command);

        if (expense.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = SharedExpenseResourceFromEntityAssembler
                .toResourceFromEntity(expense.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Add participant to shared expense",
            description = "Adds a new participant to an existing shared expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Participant added"),
            @ApiResponse(responseCode = "404", description = "Shared expense not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/{id}/participants")
    public ResponseEntity<ParticipantResource> addParticipant(
            @PathVariable Long id,
            @Valid @RequestBody AddParticipantResource resource) {

        var command = new AddParticipantCommand(
                id,
                resource.userId(),
                resource.amountOwed()
        );
        var participant = commandService.handle(command);

        if (participant.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var participantResource = ParticipantResourceFromEntityAssembler
                .toResourceFromEntity(participant.get());
        return new ResponseEntity<>(participantResource, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Mark participant as paid",
            description = "Marks a participant's payment as completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Participant marked as paid"),
            @ApiResponse(responseCode = "404", description = "Participant not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PatchMapping("/{id}/participants/{participantId}/pay")
    public ResponseEntity<ParticipantResource> markParticipantAsPaid(
            @PathVariable Long id,
            @PathVariable Long participantId) {

        var command = new MarkParticipantAsPaidCommand(participantId);
        var participant = commandService.handle(command);

        if (participant.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = ParticipantResourceFromEntityAssembler
                .toResourceFromEntity(participant.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Delete shared expense",
            description = "Deletes an existing shared expense")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Shared expense deleted"),
            @ApiResponse(responseCode = "404", description = "Shared expense not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSharedExpense(@PathVariable Long id) {
        var command = new DeleteSharedExpenseCommand(id);
        commandService.handle(command);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return authenticationHelper.getUserIdFromAuthentication(authentication);
    }
}
