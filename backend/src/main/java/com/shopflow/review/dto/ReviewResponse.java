package com.shopflow.review.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long productId,
    String productNom,
    String userNom,
    Integer note,
    String commentaire,
    boolean approuve,
    LocalDateTime dateCreation
) {}