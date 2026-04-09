package com.shopflow.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    Long id,
    BigDecimal subtotal,
    BigDecimal fraisLivraison,
    BigDecimal totalTTC,
    String couponCode,
    List<CartItemResponse> items
) {}