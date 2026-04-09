package com.shopflow.cart;

import com.shopflow.cart.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "API de gestion du panier")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Récupérer le panier", description = "Retourne le panier de l'utilisateur connecté", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        String email = authentication.getName();
        CartResponse response = cartService.getOrCreateCart(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    @Operation(summary = "Ajouter un article au panier", description = "Ajoute un produit (avec ou sans variante) au panier avec vérification du stock", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody AddItemRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        CartResponse response = cartService.addItem(email, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Modifier la quantité d'un article", description = "Met à jour la quantité d'un article dans le panier avec vérification du stock", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        CartResponse response = cartService.updateItemQuantity(email, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Supprimer un article du panier", description = "Retire un article du panier", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        CartResponse response = cartService.removeItem(email, itemId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/coupon")
    @Operation(summary = "Appliquer un code promo", description = "Applique un code promo (pourcentage ou montant fixe) au panier", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CartResponse> applyCoupon(
            @Valid @RequestBody ApplyCouponRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        CartResponse response = cartService.applyCoupon(email, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Supprimer le code promo", description = "Retire le code promo du panier", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CartResponse> removeCoupon(Authentication authentication) {
        String email = authentication.getName();
        CartResponse response = cartService.removeCoupon(email);
        return ResponseEntity.ok(response);
    }
}