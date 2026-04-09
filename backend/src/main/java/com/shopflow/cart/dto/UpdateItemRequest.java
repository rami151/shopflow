package com.shopflow.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateItemRequest(
    @NotNull @Min(1) Integer quantite
) {}