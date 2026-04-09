package com.shopflow.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
    @NotNull Long productId,
    @NotNull @Min(1) @Max(5) Integer note,
    String commentaire
) {}