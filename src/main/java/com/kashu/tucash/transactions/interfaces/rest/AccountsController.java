package com.kashu.tucash.transactions.interfaces.rest;

import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import com.kashu.tucash.transactions.domain.model.queries.GetAccountByIdQuery;
import com.kashu.tucash.transactions.domain.model.queries.GetAllAccountsByUserIdQuery;
import com.kashu.tucash.transactions.domain.services.AccountCommandService;
import com.kashu.tucash.transactions.domain.services.AccountQueryService;
import com.kashu.tucash.transactions.interfaces.rest.resources.AccountResource;
import com.kashu.tucash.transactions.interfaces.rest.resources.CreateAccountResource;
import com.kashu.tucash.transactions.interfaces.rest.transform.AccountResourceFromEntityAssembler;
import com.kashu.tucash.transactions.interfaces.rest.transform.CreateAccountCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/accounts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Accounts", description = "Endpoints de gestión de cuentas")
@SecurityRequirement(name = "bearerAuth")
public class AccountsController {

    private final AccountCommandService accountCommandService;
    private final AccountQueryService accountQueryService;
    private final AuthenticationHelper authenticationHelper;

    public AccountsController(AccountCommandService accountCommandService,
                              AccountQueryService accountQueryService,
                              AuthenticationHelper authenticationHelper) {
        this.accountCommandService = accountCommandService;
        this.accountQueryService = accountQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Obtener todas las cuentas del usuario",
            description = "Devuelve todas las cuentas del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuentas encontradas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<AccountResource>> getAllAccounts(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);

        var query = new GetAllAccountsByUserIdQuery(userId);
        var accounts = accountQueryService.handle(query);

        var resources = accounts.stream()
                .map(AccountResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Obtener cuenta por ID",
            description = "Devuelve una cuenta específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResource> getAccountById(@PathVariable Long id) {
        var query = new GetAccountByIdQuery(id);
        var account = accountQueryService.handle(query);

        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var resource = AccountResourceFromEntityAssembler.toResourceFromEntity(account.get());
        return ResponseEntity.ok(resource);
    }

    @Operation(
            summary = "Crear cuenta",
            description = "Crea una nueva cuenta para el usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<AccountResource> createAccount(
            @Valid @RequestBody CreateAccountResource resource,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        var command = CreateAccountCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var account = accountCommandService.handle(command);

        if (account.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var accountResource = AccountResourceFromEntityAssembler.toResourceFromEntity(account.get());
        return new ResponseEntity<>(accountResource, HttpStatus.CREATED);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return authenticationHelper.getUserIdFromAuthentication(authentication);
    }
}
