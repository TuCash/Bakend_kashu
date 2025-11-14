package com.kashu.tucash.automation.interfaces.rest;

import com.kashu.tucash.automation.domain.model.commands.ChangeRecurringTransactionStatusCommand;
import com.kashu.tucash.automation.domain.model.commands.DeleteRecurringTransactionCommand;
import com.kashu.tucash.automation.domain.model.queries.GetRecurringTransactionByIdQuery;
import com.kashu.tucash.automation.domain.model.queries.GetRecurringTransactionsByUserIdQuery;
import com.kashu.tucash.automation.domain.services.RecurringTransactionCommandService;
import com.kashu.tucash.automation.domain.services.RecurringTransactionQueryService;
import com.kashu.tucash.automation.interfaces.rest.resources.ChangeRecurringTransactionStatusResource;
import com.kashu.tucash.automation.interfaces.rest.resources.CreateRecurringTransactionResource;
import com.kashu.tucash.automation.interfaces.rest.resources.RecurringTransactionResource;
import com.kashu.tucash.automation.interfaces.rest.resources.UpdateRecurringTransactionResource;
import com.kashu.tucash.automation.interfaces.rest.transform.ChangeRecurringTransactionStatusCommandFromResourceAssembler;
import com.kashu.tucash.automation.interfaces.rest.transform.CreateRecurringTransactionCommandFromResourceAssembler;
import com.kashu.tucash.automation.interfaces.rest.transform.RecurringTransactionResourceFromEntityAssembler;
import com.kashu.tucash.automation.interfaces.rest.transform.UpdateRecurringTransactionCommandFromResourceAssembler;
import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/recurring-transactions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Recurring Transactions", description = "Automated transaction scheduling")
@SecurityRequirement(name = "bearerAuth")
public class RecurringTransactionsController {

    private final RecurringTransactionCommandService commandService;
    private final RecurringTransactionQueryService queryService;
    private final AuthenticationHelper authenticationHelper;

    public RecurringTransactionsController(RecurringTransactionCommandService commandService,
                                           RecurringTransactionQueryService queryService,
                                           AuthenticationHelper authenticationHelper) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(summary = "List recurring transactions for authenticated user")
    @GetMapping
    public ResponseEntity<List<RecurringTransactionResource>> getRecurringTransactions(Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var query = new GetRecurringTransactionsByUserIdQuery(userId);
        var entities = queryService.handle(query);
        var resources = entities.stream()
                .map(RecurringTransactionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get recurring transaction by id")
    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionResource> getRecurringTransactionById(@PathVariable Long id) {
        var query = new GetRecurringTransactionByIdQuery(id);
        var entity = queryService.handle(query);
        return entity
                .map(RecurringTransactionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create recurring transaction")
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<RecurringTransactionResource> createRecurringTransaction(
            @Valid @RequestBody CreateRecurringTransactionResource resource,
            Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var command = CreateRecurringTransactionCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var result = commandService.handle(command);
        return result
                .map(RecurringTransactionResourceFromEntityAssembler::toResourceFromEntity)
                .map(r -> new ResponseEntity<>(r, HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Update recurring transaction")
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<RecurringTransactionResource> updateRecurringTransaction(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecurringTransactionResource resource) {
        var command = UpdateRecurringTransactionCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = commandService.handle(command);
        return result
                .map(RecurringTransactionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Pause or resume recurring transaction")
    @PatchMapping(value = "/{id}/status", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<RecurringTransactionResource> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRecurringTransactionStatusResource resource) {
        ChangeRecurringTransactionStatusCommand command =
                ChangeRecurringTransactionStatusCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = commandService.handle(command);
        return result
                .map(RecurringTransactionResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete recurring transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringTransaction(@PathVariable Long id) {
        commandService.handle(new DeleteRecurringTransactionCommand(id));
        return ResponseEntity.noContent().build();
    }
}
