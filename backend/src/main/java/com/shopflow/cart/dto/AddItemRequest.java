package com.shopflow.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(
    @NotNull Long productId,
    Long variantId,
    @NotNull @Min(1) Integer quantite
) {}