package com.shopflow.product.dto;

import java.math.BigDecimal;

public record ProductVariantDto(
    Long id,
    String couleur,
    String taille,
    BigDecimal prixSupplementaire,
    Integer stockSupplementaire,
    Boolean actif
) {}
