package com.shopflow.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

public record UpdateProductRequest(
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    String nom,

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
    String description,

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    BigDecimal prix,

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock doit être positif ou nul")
    Integer stock,

    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    String imageUrl,

    Set<Long> categoryIds
) {}
