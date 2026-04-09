package com.shopflow.order.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull Long addressId
) {}