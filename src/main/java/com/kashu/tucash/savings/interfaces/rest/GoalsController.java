package com.kashu.tucash.savings.interfaces.rest;

import com.kashu.tucash.savings.domain.model.commands.*;
import com.kashu.tucash.savings.domain.model.queries.*;
import com.kashu.tucash.savings.domain.services.*;
import com.kashu.tucash.savings.interfaces.rest.resources.*;
import com.kashu.tucash.savings.interfaces.rest.transform.*;
import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import com.kashu.tucash.transactions.interfaces.rest.resources.TransactionResource;
import com.kashu.tucash.transactions.interfaces.rest.transform.TransactionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping(value = "/api/v1/goals", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Goals", description = "Endpoints de gestión de metas de ahorro")
@SecurityRequirement(name = "bearerAuth")
public class GoalsController {

    private final GoalCommandService goalCommandService;
    private final GoalQueryService goalQueryService;
    private final AuthenticationHelper authenticationHelper;

    public GoalsController(GoalCommandService goalCommandService,
                          GoalQueryService goalQueryService,
                          AuthenticationHelper authenticationHelper) {
        this.goalCommandService = goalCommandService;
        this.goalQueryService = goalQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(summary = "Obtener todas las metas del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Metas encontradas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<GoalResource>> getAllGoals(Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var query = new GetAllGoalsByUserIdQuery(userId);
        var goals = goalQueryService.handle(query);
        var resources = goals.stream()
                .map(GoalResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Obtener meta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<GoalResource> getGoalById(@PathVariable Long id) {
        var query = new GetGoalByIdQuery(id);
        var goal = goalQueryService.handle(query);
        if (goal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = GoalResourceFromEntityAssembler.toResourceFromEntity(goal.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(summary = "Crear meta")
    @PostMapping
    public ResponseEntity<GoalResource> createGoal(
            @Valid @RequestBody CreateGoalResource resource,
            Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var command = CreateGoalCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var goal = goalCommandService.handle(command);
        if (goal.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var goalResource = GoalResourceFromEntityAssembler.toResourceFromEntity(goal.get());
        return new ResponseEntity<>(goalResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar progreso de meta")
    @PatchMapping("/{id}/progress")
    public ResponseEntity<GoalResource> updateGoalProgress(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGoalProgressResource resource) {
        var command = new UpdateGoalProgressCommand(id, resource.currentAmount());
        var goal = goalCommandService.handle(command);
        if (goal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var goalResource = GoalResourceFromEntityAssembler.toResourceFromEntity(goal.get());
        return ResponseEntity.ok(goalResource);
    }

    @Operation(summary = "Celebrar meta completada")
    @PostMapping("/{id}/celebrate")
    public ResponseEntity<GoalResource> celebrateGoal(@PathVariable Long id) {
        var command = new CelebrateGoalCommand(id);
        var goal = goalCommandService.handle(command);
        if (goal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var goalResource = GoalResourceFromEntityAssembler.toResourceFromEntity(goal.get());
        return ResponseEntity.ok(goalResource);
    }

    @Operation(summary = "Eliminar meta")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        var command = new DeleteGoalCommand(id);
        goalCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }

    // ==================== CONTRIBUCIONES ====================

    @Operation(summary = "Añadir contribución a meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contribución creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Meta o cuenta no encontrada")
    })
    @PostMapping("/{id}/contributions")
    public ResponseEntity<TransactionResource> contributeToGoal(
            @PathVariable Long id,
            @Valid @RequestBody ContributeToGoalResource resource,
            Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var command = new ContributeToGoalCommand(
                userId,
                id,
                resource.accountId(),
                resource.amount(),
                resource.description()
        );
        var transaction = goalCommandService.handle(command);
        if (transaction.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
        return new ResponseEntity<>(transactionResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener contribuciones de una meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contribuciones encontradas"),
            @ApiResponse(responseCode = "404", description = "Meta no encontrada")
    })
    @GetMapping("/{id}/contributions")
    public ResponseEntity<List<TransactionResource>> getGoalContributions(@PathVariable Long id) {
        var contributions = goalCommandService.getGoalContributions(id);
        var resources = contributions.stream()
                .map(TransactionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Revertir contribución de una meta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contribución revertida"),
            @ApiResponse(responseCode = "404", description = "Meta o transacción no encontrada")
    })
    @DeleteMapping("/{id}/contributions/{transactionId}")
    public ResponseEntity<TransactionResource> revertContribution(
            @PathVariable Long id,
            @PathVariable Long transactionId,
            Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);
        var command = new RevertGoalContributionCommand(userId, id, transactionId);
        var transaction = goalCommandService.handle(command);
        if (transaction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transaction.get());
        return ResponseEntity.ok(transactionResource);
    }
}
