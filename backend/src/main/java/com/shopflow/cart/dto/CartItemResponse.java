package com.shopflow.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
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