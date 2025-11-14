package com.kashu.tucash.savings.interfaces.rest;

import com.kashu.tucash.savings.domain.model.commands.*;
import com.kashu.tucash.savings.domain.model.queries.*;
import com.kashu.tucash.savings.domain.services.*;
import com.kashu.tucash.savings.interfaces.rest.resources.*;
import com.kashu.tucash.savings.interfaces.rest.transform.*;
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
@RequestMapping(value = "/api/v1/budgets", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Budgets", description = "Endpoints de gestión de presupuestos")
@SecurityRequirement(name = "bearerAuth")
public class BudgetsController {

    private final BudgetCommandService budgetCommandService;
    private final BudgetQueryService budgetQueryService;
    private final AuthenticationHelper authenticationHelper;

    public BudgetsController(BudgetCommandService budgetCommandService,
                            BudgetQueryService budgetQueryService,
                            AuthenticationHelper authenticationHelper) {
        this.budgetCommandService = budgetCommandService;
        this.budgetQueryService = budgetQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(summary = "Obtener todos los presupuestos del usuario")
    @GetMapping
    public ResponseEntity<List<BudgetResource>> getAllBudgets(Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var query = new GetAllBudgetsByUserIdQuery(userId);
        var budgets = budgetQueryService.handle(query);
        var resources = budgets.stream()
                .map(BudgetResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Obtener presupuesto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<BudgetResource> getBudgetById(@PathVariable Long id) {
        var query = new GetBudgetByIdQuery(id);
        var budget = budgetQueryService.handle(query);
        if (budget.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = BudgetResourceFromEntityAssembler.toResourceFromEntity(budget.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Crear presupuesto")
    @PostMapping
    public ResponseEntity<BudgetResource> createBudget(
            @Valid @RequestBody CreateBudgetResource resource,
            Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var command = CreateBudgetCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var budget = budgetCommandService.handle(command);
        if (budget.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var budgetResource = BudgetResourceFromEntityAssembler.toResourceFromEntity(budget.get());
        return new ResponseEntity<>(budgetResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar presupuesto")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        var command = new DeleteBudgetCommand(id);
        budgetCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }
}
