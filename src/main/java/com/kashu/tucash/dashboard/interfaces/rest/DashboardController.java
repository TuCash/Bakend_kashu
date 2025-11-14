package com.kashu.tucash.dashboard.interfaces.rest;

import com.kashu.tucash.dashboard.domain.services.DashboardQueryService;
import com.kashu.tucash.dashboard.interfaces.rest.resources.*;
import com.kashu.tucash.shared.infrastructure.security.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/dashboard", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Dashboard", description = "Endpoints de analíticas y resúmenes financieros")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardQueryService dashboardQueryService;
    private final AuthenticationHelper authenticationHelper;

    public DashboardController(DashboardQueryService dashboardQueryService,
                              AuthenticationHelper authenticationHelper) {
        this.dashboardQueryService = dashboardQueryService;
        this.authenticationHelper = authenticationHelper;
    }

    @Operation(
            summary = "Obtener pulso financiero",
            description = "Devuelve resumen de ingresos, gastos, balance y tasa de ahorro para el período especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pulso obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/pulse")
    public ResponseEntity<DashboardPulseResource> getPulse(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd). Por defecto: inicio del mes actual")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd). Por defecto: fecha actual")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

            Authentication authentication) {

        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        // Valores por defecto: mes actual
        LocalDate start = fromDate != null ? fromDate : YearMonth.now().atDay(1);
        LocalDate end = toDate != null ? toDate : LocalDate.now();

        var pulse = dashboardQueryService.getPulse(userId, start, end);
        return ResponseEntity.ok(pulse);
    }

    @Operation(
            summary = "Obtener tendencias mensuales",
            description = "Devuelve tendencias de ingresos y gastos por mes para los últimos N meses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tendencias obtenidas exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/trends")
    public ResponseEntity<TrendSeriesResource> getTrends(
            @Parameter(description = "Número de meses hacia atrás (por defecto: 6)")
            @RequestParam(defaultValue = "6") int months,

            Authentication authentication) {

        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        var trends = dashboardQueryService.getTrends(userId, months);
        return ResponseEntity.ok(trends);
    }

    @Operation(
            summary = "Obtener fugas por categoría",
            description = "Devuelve las categorías con mayor gasto (gastos hormiga) para el período especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fugas obtenidas exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/leaks")
    public ResponseEntity<CategoryLeaksResource> getLeaks(
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd). Por defecto: inicio del mes actual")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd). Por defecto: fecha actual")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

            @Parameter(description = "Número de categorías top a mostrar (por defecto: 5)")
            @RequestParam(defaultValue = "5") int top,

            Authentication authentication) {

        Long userId = authenticationHelper.getUserIdFromAuthentication(authentication);

        // Valores por defecto: mes actual
        LocalDate start = fromDate != null ? fromDate : YearMonth.now().atDay(1);
        LocalDate end = toDate != null ? toDate : LocalDate.now();

        var leaks = dashboardQueryService.getLeaks(userId, start, end, top);
        return ResponseEntity.ok(leaks);
    }
}
