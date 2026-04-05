package com.shopflow.product.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductDto(
    Long id,
    String nom,
    String description,
    BigDecimal prix,
    Integer stock,
    String imageUrl,
    Boolean actif,
    Long sellerProfileId,
    String sellerName,
    Set<CategoryDto> categories,
    List<ProductVariantDto> variants,
    Double averageRating,
    Integer reviewCount
) {}
