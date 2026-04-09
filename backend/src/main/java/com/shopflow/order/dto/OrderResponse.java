package com.shopflow.order.dto;

import com.shopflow.shared.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    String numeroCommande,
    OrderStatus statut,
    BigDecimal subtotal,
    BigDecimal fraisLivraison,
    BigDecimal totalTTC,
    String couponCode,
    BigDecimal couponDiscount,
    LocalDateTime dateCommande,
    AddressResponse adresseLivraison,
    List<OrderItemResponse> items
) {}