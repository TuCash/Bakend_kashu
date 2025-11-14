package com.kashu.tucash.transactions.interfaces.rest;

import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import com.kashu.tucash.transactions.domain.model.queries.GetAllCategoriesByUserIdAndTypeQuery;
import com.kashu.tucash.transactions.domain.model.valueobjects.CategoryType;
import com.kashu.tucash.transactions.domain.services.CategoryCommandService;
import com.kashu.tucash.transactions.domain.services.CategoryQueryService;
import com.kashu.tucash.transactions.interfaces.rest.resources.CategoryResource;
import com.kashu.tucash.transactions.interfaces.rest.resources.CreateCategoryResource;
import com.kashu.tucash.transactions.interfaces.rest.transform.CategoryResourceFromEntityAssembler;
import com.kashu.tucash.transactions.interfaces.rest.transform.CreateCategoryCommandFromResourceAssembler;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/categories", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Categories", description = "Endpoints de gestión de categorías")
@SecurityRequirement(name = "bearerAuth")
public class CategoriesController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;
    private final AuthenticationHelper authenticationHelper;

    public CategoriesController(CategoryCommandService categoryCommandService,
                                CategoryQueryService categoryQueryService,
                                AuthenticationHelper authenticationHelper) {
        this.categoryCommandService = categoryCommandService;
        this.categoryQueryService = categoryQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Obtener categorías por tipo",
            description = "Devuelve las categorías del sistema y del usuario filtradas por tipo (INCOME o EXPENSE)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorías encontradas"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResource>> getCategories(
            @Parameter(description = "Tipo de categoría: INCOME o EXPENSE")
            @RequestParam(required = false, defaultValue = "EXPENSE") String type,
            Authentication authentication) {

        // Obtener userId del usuario autenticado
        Long userId = getUserIdFromAuthentication(authentication);

        var query = new GetAllCategoriesByUserIdAndTypeQuery(userId, CategoryType.valueOf(type.toUpperCase()));
        var categories = categoryQueryService.handle(query);

        var resources = categories.stream()
                .map(CategoryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(resources);
    }

    @Operation(
            summary = "Crear categoría personalizada",
            description = "Crea una nueva categoría personalizada para el usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<CategoryResource> createCategory(
            @Valid @RequestBody CreateCategoryResource resource,
            Authentication authentication) {

        Long userId = getUserIdFromAuthentication(authentication);

        var command = CreateCategoryCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var category = categoryCommandService.handle(command);

        if (category.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var categoryResource = CategoryResourceFromEntityAssembler.toResourceFromEntity(category.get());
        return new ResponseEntity<>(categoryResource, HttpStatus.CREATED);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return authenticationHelper.getUserIdFromAuthentication(authentication);
    }
}
