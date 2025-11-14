package com.kashu.tucash.iam.interfaces.rest;

import com.kashu.tucash.iam.domain.services.UserCommandService;
import com.kashu.tucash.iam.interfaces.rest.resources.*;
import com.kashu.tucash.iam.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/auth", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Endpoints de autenticación y registro")
public class AuthenticationController {

    private final UserCommandService userCommandService;

    public AuthenticationController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en la plataforma TuCash")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResource> register(@Valid @RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        var user = userCommandService.handle(command);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "401", description = "Usuario o contraseña incorrectos")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticatedUserResource> login(@Valid @RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var authenticatedUser = userCommandService.handle(command);

        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = authenticatedUser.get().getLeft();
        var token = authenticatedUser.get().getRight();

        var authResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, token);
        return ResponseEntity.ok(authResource);
    }

    @Operation(
            summary = "Solicitar reseteo de contraseña",
            description = "Genera un token de reseteo y lo devuelve (en producción se enviaría por email)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token de reseteo generado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Email inválido")
    })
    @PostMapping(value = "/forgot-password", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PasswordResetResponseResource> requestPasswordReset(
            @Valid @RequestBody RequestPasswordResetResource resource) {

        try {
            var command = RequestPasswordResetCommandFromResourceAssembler.toCommandFromResource(resource);
            var token = userCommandService.handle(command);

            if (token.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new PasswordResetResponseResource("Error al generar token de reseteo"));
            }

            // In production, send email with reset link instead of returning token
            // For development/testing, we return the token directly
            return ResponseEntity.ok(new PasswordResetResponseResource(
                    "Token de reseteo: " + token.get() + " (válido por 1 hora)"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new PasswordResetResponseResource(e.getMessage()));
        }
    }

    @Operation(
            summary = "Resetear contraseña",
            description = "Resetea la contraseña del usuario usando un token válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña reseteada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
    })
    @PostMapping(value = "/reset-password", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PasswordResetResponseResource> resetPassword(
            @Valid @RequestBody ResetPasswordResource resource) {

        try {
            var command = ResetPasswordCommandFromResourceAssembler.toCommandFromResource(resource);
            var user = userCommandService.handle(command);

            if (user.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new PasswordResetResponseResource("Error al resetear contraseña"));
            }

            return ResponseEntity.ok(new PasswordResetResponseResource(
                    "Contraseña reseteada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new PasswordResetResponseResource(e.getMessage()));
        }
    }
}
