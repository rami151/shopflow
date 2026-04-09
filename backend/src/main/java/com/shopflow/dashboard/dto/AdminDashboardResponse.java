package com.shopflow.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record AdminDashboardResponse(
    BigDecimal chiffreAffairesGlobal,
    Integer totalCommandes,
    Integer totalProduits,
    Integer totalVendeurs,
    Integer totalClients,
    List<ProductStat> topProduits,
    List<SellerStat> topVendeurs,
    List<OrderSummary> commandesRecentes
) {
    public record ProductStat(
        Long id,
        String nom,
        Integer quantiteVendue,
        BigDecimal revenu
    ) {}

    public record SellerStat(
        Long id,
        String nom,
        BigDecimal revenu,
        Integer commandes
    ) {}

    public record OrderSummary(
        Long id,
        String numeroCommande,
        String clientNom,
        BigDecimal total,
        String statut,
        java.time.LocalDateTime dateCommande
    ) {}
}