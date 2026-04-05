package com.shopflow.product.dto;

import java.math.BigDecimal;

public record ProductSummaryDto(
    Long id,
    String nom,
    BigDecimal prix,
    String imageUrl,
    Integer stock,
    String sellerName,
    Double averageRating
) {}
