package com.shopflow.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateProductVariantRequest(
    @NotBlank(message = "La couleur est obligatoire")
    String couleur,

    @NotBlank(message = "La taille est obligatoire")
    String taille,

    @DecimalMin(value = "0.0", message = "Le prix supplémentaire doit être positif ou nul")
    BigDecimal prixSupplementaire,

    @Min(value = 0, message = "Le stock supplémentaire doit être positif ou nul")
    Integer stockSupplementaire
) {}
