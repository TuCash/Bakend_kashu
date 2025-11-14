package com.kashu.tucash.reminders.interfaces.rest;

import com.kashu.tucash.reminders.domain.model.commands.DeleteReminderCommand;
import com.kashu.tucash.reminders.domain.model.queries.GetActiveRemindersQuery;
import com.kashu.tucash.reminders.domain.model.queries.GetAllRemindersByUserIdQuery;
import com.kashu.tucash.reminders.domain.model.queries.GetReminderByIdQuery;
import com.kashu.tucash.reminders.domain.model.valueobjects.ReminderType;
import com.kashu.tucash.reminders.domain.services.ReminderCommandService;
import com.kashu.tucash.reminders.domain.services.ReminderQueryService;
import com.kashu.tucash.reminders.interfaces.rest.resources.CreateReminderResource;
import com.kashu.tucash.reminders.interfaces.rest.resources.ReminderResource;
import com.kashu.tucash.reminders.interfaces.rest.resources.UpdateReminderResource;
import com.kashu.tucash.reminders.interfaces.rest.transform.CreateReminderCommandFromResourceAssembler;
import com.kashu.tucash.reminders.interfaces.rest.transform.ReminderResourceFromEntityAssembler;
import com.kashu.tucash.reminders.interfaces.rest.transform.UpdateReminderCommandFromResourceAssembler;
import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/reminders", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Reminders", description = "Endpoints de gestion de recordatorios")
@SecurityRequirement(name = "bearerAuth")
public class RemindersController {

    private final ReminderCommandService reminderCommandService;
    private final ReminderQueryService reminderQueryService;
    private final AuthenticationHelper authenticationHelper;

    public RemindersController(ReminderCommandService reminderCommandService,
                               ReminderQueryService reminderQueryService,
                               AuthenticationHelper authenticationHelper) {
        this.reminderCommandService = reminderCommandService;
        this.reminderQueryService = reminderQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Obtener todos los recordatorios",
            description = "Devuelve todos los recordatorios del usuario autenticado con filtros opcionales"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorios encontrados"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<ReminderResource>> getAllReminders(
            @Parameter(description = "Tipo de recordatorio: PAYMENT, BILL, GOAL, BUDGET, SAVINGS, CUSTOM")
            @RequestParam(required = false) String type,

            @Parameter(description = "Estado activo del recordatorio")
            @RequestParam(required = false) Boolean isActive,

            Authentication authentication) {

        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        ReminderType reminderType = type != null ? ReminderType.valueOf(type.toUpperCase()) : null;

        var query = new GetAllRemindersByUserIdQuery(userId, reminderType, isActive);
        var reminders = reminderQueryService.handle(query);
        var resources = reminders.stream()
                .map(ReminderResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Obtener recordatorios activos",
            description = "Devuelve todos los recordatorios activos pendientes de notificacion del usuario"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorios activos encontrados"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/active")
    public ResponseEntity<List<ReminderResource>> getActiveReminders(Authentication authentication) {
        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        var query = new GetActiveRemindersQuery(userId);
        var reminders = reminderQueryService.handle(query);
        var resources = reminders.stream()
                .map(ReminderResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Obtener recordatorio por ID",
            description = "Devuelve un recordatorio especifico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorio encontrado"),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReminderResource> getReminderById(@PathVariable Long id) {
        var query = new GetReminderByIdQuery(id);
        var reminder = reminderQueryService.handle(query);

        if (reminder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = ReminderResourceFromEntityAssembler.toResourceFromEntity(reminder.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Crear recordatorio",
            description = "Crea un nuevo recordatorio para el usuario autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recordatorio creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<ReminderResource> createReminder(
            @Valid @RequestBody CreateReminderResource resource,
            Authentication authentication) {

        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        var command = CreateReminderCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var reminder = reminderCommandService.handle(command);

        if (reminder.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var reminderResource = ReminderResourceFromEntityAssembler.toResourceFromEntity(reminder.get());
        return new ResponseEntity<>(reminderResource, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar recordatorio",
            description = "Actualiza un recordatorio existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recordatorio actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReminderResource> updateReminder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReminderResource resource) {

        var command = UpdateReminderCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var reminder = reminderCommandService.handle(command);

        if (reminder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var reminderResource = ReminderResourceFromEntityAssembler.toResourceFromEntity(reminder.get());
        return ResponseEntity.ok(reminderResource);
    }

    @Operation(
            summary = "Eliminar recordatorio",
            description = "Elimina un recordatorio existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Recordatorio eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Recordatorio no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        var command = new DeleteReminderCommand(id);
        reminderCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }
}
