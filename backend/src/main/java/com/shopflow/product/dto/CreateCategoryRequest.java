package com.shopflow.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    String nom,

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    String description,

    Long parentId
) {}
