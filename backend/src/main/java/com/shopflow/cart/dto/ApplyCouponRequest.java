package com.shopflow.cart.dto;

import jakarta.validation.constraints.NotBlank;

public record ApplyCouponRequest(
    @NotBlank String code
) {}