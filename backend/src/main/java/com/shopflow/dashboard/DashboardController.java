package com.shopflow.dashboard;

import com.shopflow.dashboard.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "API du tableau de bord personnalisé par rôle")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(
        summary = "Récupérer le tableau de bord",
        description = "Retourne les données du dashboard selon le rôle de l'utilisateur: " +
                      "ADMIN (chiffre d'affaires, top produits/vendeurs, commandes récentes), " +
                      "SELLER (revenus, commandes en attente, alertes stock), " +
                      "CUSTOMER (commandes en cours, derniers avis)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Object> getDashboard(Authentication authentication) {
        String email = authentication.getName();
        Object dashboard = dashboardService.getDashboard(email);
        return ResponseEntity.ok(dashboard);
    }
}