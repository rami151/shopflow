package com.shopflow.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record SellerDashboardResponse(
    BigDecimal revenuTotal,
    Integer commandesEnAttente,
    Integer commandesLivrees,
    List<ProductAlert> alertesStock,
    List<OrderSummary> commandesRecentes
) {
    public record ProductAlert(
        Long id,
        String nom,
        Integer stockActuel,
        String niveau
    ) {}

    public record OrderSummary(
        Long id,
        String numeroCommande,
        BigDecimal total,
        String statut,
        java.time.LocalDateTime dateCommande
    ) {}
}