package com.shopflow.product.dto;

public record CategoryDto(
    Long id,
    String nom,
    String description,
    Long parentId,
    String parentNom,
    Boolean actif
) {}
