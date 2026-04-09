package com.shopflow.dashboard.dto;

import java.util.List;

public record CustomerDashboardResponse(
    List<OrderSummary> commandesEnCours,
    List<ReviewSummary> derniersAvis
) {
    public record OrderSummary(
        Long id,
        String numeroCommande,
        java.math.BigDecimal total,
        String statut,
        java.time.LocalDateTime dateCommande
    ) {}

    public record ReviewSummary(
        Long id,
        Long produitId,
        String produitNom,
        String produitImage,
        Integer note,
        String commentaire,
        java.time.LocalDateTime dateCreation
    ) {}
}