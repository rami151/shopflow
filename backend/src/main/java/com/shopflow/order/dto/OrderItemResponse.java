package com.shopflow.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long id,
    Long productId,
    String productName,
    String productImageUrl,
    Long variantId,
    String variantNom,
    String variantValeur,
    BigDecimal prixUnitaire,
    Integer quantite,
    BigDecimal sousTotal
) {}