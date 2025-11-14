package com.kashu.tucash.iam.interfaces.rest;

import com.kashu.tucash.iam.domain.model.queries.GetUserByIdQuery;
import com.kashu.tucash.iam.domain.services.UserCommandService;
import com.kashu.tucash.iam.domain.services.UserQueryService;
import com.kashu.tucash.iam.interfaces.rest.resources.*;
import com.kashu.tucash.iam.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/users", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Endpoints de gestión de usuarios")
public class UsersController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    public UsersController(UserQueryService userQueryService, UserCommandService userCommandService) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve la información de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResource> getUserById(@PathVariable Long id) {
        var query = new GetUserByIdQuery(id);
        var user = userQueryService.handle(query);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    @Operation(
            summary = "Actualizar perfil de usuario",
            description = "Actualiza la información del perfil del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserResource> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserResource resource) {

        var command = UpdateUserCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var user = userCommandService.handle(command);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    @Operation(
            summary = "Obtener preferencias de usuario",
            description = "Devuelve las preferencias y configuraciones del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencias obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}/preferences")
    public ResponseEntity<UserPreferencesResource> getUserPreferences(@PathVariable Long id) {
        var query = new GetUserByIdQuery(id);
        var user = userQueryService.handle(query);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var preferencesResource = UserPreferencesResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(preferencesResource);
    }

    @Operation(
            summary = "Actualizar preferencias de usuario",
            description = "Actualiza las preferencias y configuraciones del usuario (moneda, tema, idioma, notificaciones)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferencias actualizadas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/{id}/preferences")
    public ResponseEntity<UserPreferencesResource> updateUserPreferences(
            @PathVariable Long id,
            @RequestBody UpdateUserPreferencesResource resource) {

        var command = UpdateUserPreferencesCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var user = userCommandService.handle(command);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var preferencesResource = UserPreferencesResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(preferencesResource);
    }

    @Operation(
            summary = "Cambiar contraseña",
            description = "Permite al usuario cambiar su contraseña proporcionando la contraseña actual y la nueva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping(value = "/{id}/password", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PasswordResetResponseResource> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordResource resource) {

        try {
            var command = ChangePasswordCommandFromResourceAssembler.toCommandFromResource(id, resource);
            var user = userCommandService.handle(command);

            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(new PasswordResetResponseResource("Contraseña actualizada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new PasswordResetResponseResource(e.getMessage()));
        }
    }
}
