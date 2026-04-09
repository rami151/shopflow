package com.shopflow.review;

import com.shopflow.review.dto.CreateReviewRequest;
import com.shopflow.review.dto.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "API de gestion des avis")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Laisser un avis", description = "Laisser un avis (note 1-5 + commentaire) sur un produit acheté. L'avis doit être approuvé par un admin pour être visible", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        ReviewResponse response = reviewService.createReview(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Récupérer les avis d'un produit", description = "Retourne la liste des avis approuvés pour un produit avec la note moyenne")
    public ResponseEntity<Map<String, Object>> getProductReviews(
            @Parameter(description = "ID du produit") @PathVariable Long productId,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponse> reviews = reviewService.getProductReviewsPaged(productId, PageRequest.of(page, size));
        Double averageRating = reviewService.getProductAverageRating(productId);
        Long reviewCount = reviewService.getProductReviewCount(productId);
        
        return ResponseEntity.ok(Map.of(
                "avis", reviews.getContent(),
                "noteMoyenne", averageRating,
                "nombreAvis", reviewCount,
                "page", reviews.getNumber(),
                "totalPages", reviews.getTotalPages(),
                "totalElements", reviews.getTotalElements()
        ));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer les avis en attente de modération", description = "Retourne la liste des avis non approuvés (réservé ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<ReviewResponse>> getPendingReviews(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponse> reviews = reviewService.getPendingReviews(PageRequest.of(page, size));
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{reviewId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approuver un avis", description = "Approuve un avis pour le rendre visible (réservé ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> approveReview(
            @Parameter(description = "ID de l'avis") @PathVariable Long reviewId
    ) {
        ReviewResponse response = reviewService.approveReview(reviewId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reviewId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rejeter un avis", description = "Rejette et supprime un avis (réservé ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> rejectReview(
            @Parameter(description = "ID de l'avis") @PathVariable Long reviewId
    ) {
        reviewService.rejectReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Supprimer mon avis", description = "Supprime son propre avis")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID de l'avis") @PathVariable Long reviewId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        reviewService.deleteReview(reviewId, email);
        return ResponseEntity.noContent().build();
    }
}