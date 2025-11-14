package com.kashu.tucash.notifications.interfaces.rest;

import com.kashu.tucash.notifications.domain.model.commands.DeleteNotificationCommand;
import com.kashu.tucash.notifications.domain.model.commands.MarkAsReadCommand;
import com.kashu.tucash.notifications.domain.model.queries.GetAllNotificationsByUserIdQuery;
import com.kashu.tucash.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.kashu.tucash.notifications.domain.model.queries.GetUnreadNotificationsByUserIdQuery;
import com.kashu.tucash.notifications.domain.model.valueobjects.NotificationType;
import com.kashu.tucash.notifications.domain.services.NotificationCommandService;
import com.kashu.tucash.notifications.domain.services.NotificationQueryService;
import com.kashu.tucash.notifications.interfaces.rest.resources.CreateNotificationResource;
import com.kashu.tucash.notifications.interfaces.rest.resources.NotificationResource;
import com.kashu.tucash.notifications.interfaces.rest.transform.CreateNotificationCommandFromResourceAssembler;
import com.kashu.tucash.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Endpoints de gestion de notificaciones")
@SecurityRequirement(name = "bearerAuth")
public class NotificationsController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;
    private final AuthenticationHelper authenticationHelper;

    public NotificationsController(NotificationCommandService notificationCommandService,
                                   NotificationQueryService notificationQueryService,
                                   AuthenticationHelper authenticationHelper) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Obtener todas las notificaciones",
            description = "Devuelve todas las notificaciones del usuario autenticado con filtros opcionales y paginacion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones encontradas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<Page<NotificationResource>> getAllNotifications(
            @Parameter(description = "Tipo de notificacion: INFO, WARNING, SUCCESS, ERROR, REMINDER, BUDGET_ALERT, GOAL_ACHIEVED")
            @RequestParam(required = false) String type,

            @Parameter(description = "Estado de lectura de la notificacion")
            @RequestParam(required = false) Boolean isRead,

            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,

            Authentication authentication) {

        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        NotificationType notificationType = type != null ? NotificationType.valueOf(type.toUpperCase()) : null;

        var query = new GetAllNotificationsByUserIdQuery(userId, notificationType, isRead);
        var notifications = notificationQueryService.handle(query, pageable);
        var resources = notifications.map(NotificationResourceFromEntityAssembler::toResourceFromEntity);

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Obtener notificaciones no leidas",
            description = "Devuelve todas las notificaciones no leidas del usuario autenticado con paginacion"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones no leidas encontradas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/unread")
    public ResponseEntity<Page<NotificationResource>> getUnreadNotifications(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {

        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        var query = new GetUnreadNotificationsByUserIdQuery(userId);
        var notifications = notificationQueryService.handle(query, pageable);
        var resources = notifications.map(NotificationResourceFromEntityAssembler::toResourceFromEntity);

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Obtener notificacion por ID",
            description = "Devuelve una notificacion especifica por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificacion encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificacion no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long id) {
        var query = new GetNotificationByIdQuery(id);
        var notification = notificationQueryService.handle(query);

        if (notification.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = NotificationResourceFromEntityAssembler.toResourceFromEntity(notification.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Crear notificacion",
            description = "Crea una nueva notificacion (solo administradores)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificacion creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResource> createNotification(
            @Valid @RequestBody CreateNotificationResource resource) {

        var command = CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        var notification = notificationCommandService.handle(command);

        if (notification.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var notificationResource = NotificationResourceFromEntityAssembler.toResourceFromEntity(notification.get());
        return new ResponseEntity<>(notificationResource, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Marcar notificacion como leida",
            description = "Marca una notificacion existente como leida"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificacion marcada como leida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Notificacion no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResource> markAsRead(@PathVariable Long id) {
        var command = new MarkAsReadCommand(id);
        var notification = notificationCommandService.handle(command);

        if (notification.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var notificationResource = NotificationResourceFromEntityAssembler.toResourceFromEntity(notification.get());
        return ResponseEntity.ok(notificationResource);
    }

    @Operation(
            summary = "Eliminar notificacion",
            description = "Elimina una notificacion existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificacion eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Notificacion no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        var command = new DeleteNotificationCommand(id);
        notificationCommandService.handle(command);
        return ResponseEntity.noContent().build();
    }
}
