package com.kashu.tucash.savings.interfaces.rest;

import com.kashu.tucash.savings.domain.model.commands.*;
import com.kashu.tucash.savings.domain.model.queries.*;
import com.kashu.tucash.savings.domain.services.*;
import com.kashu.tucash.savings.interfaces.rest.resources.*;
import com.kashu.tucash.savings.interfaces.rest.transform.*;
import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
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
}
