package com.shopflow.order.dto;

public record AddressResponse(
    Long id,
    String rue,
    String ville,
    String codePostal,
    String pays,
    boolean principale
) {}