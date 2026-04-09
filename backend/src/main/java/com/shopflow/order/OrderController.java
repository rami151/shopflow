package com.shopflow.order;

import com.shopflow.order.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "API de gestion des commandes")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Passer une commande", description = "Crée une commande depuis le panier avec vérification du stock", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        OrderResponse response = orderService.createOrder(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "Simuler le paiement", description = "Simule le paiement d'une commande (PENDING -> PAID)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> simulatePayment(
            @Parameter(description = "ID de la commande") @PathVariable Long orderId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        OrderResponse response = orderService.simulatePayment(email, orderId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Mettre à jour le statut", description = "Met à jour le statut de la commande (simulation du flow: PAID -> PROCESSING -> SHIPPED -> DELIVERED)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> updateStatus(
            @Parameter(description = "ID de la commande") @PathVariable Long orderId,
            @Parameter(description = "Nouveau statut") @RequestParam String status,
            Authentication authentication
    ) {
        String email = authentication.getName();
        com.shopflow.shared.enums.OrderStatus newStatus = com.shopflow.shared.enums.OrderStatus.valueOf(status.toUpperCase());
        OrderResponse response = orderService.updateStatus(email, orderId, newStatus);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Annuler une commande", description = "Annule une commande (seulement si PENDING ou PAID). Restore le stock et décrémente l'usage du coupon", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "ID de la commande") @PathVariable Long orderId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        OrderResponse response = orderService.cancelOrder(email, orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes mes commandes", description = "Retourne la liste paginée des commandes de l'utilisateur", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> response = orderService.getUserOrdersPaged(email, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Récupérer une commande par ID", description = "Retourne les détails d'une commande spécifique", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID de la commande") @PathVariable Long orderId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        OrderResponse response = orderService.getOrderById(email, orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/addresses")
    @Operation(summary = "Récupérer mes adresses", description = "Retourne la liste des adresses de livraison sauvegardées", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<AddressResponse>> getMyAddresses(Authentication authentication) {
        String email = authentication.getName();
        List<AddressResponse> response = orderService.getUserAddresses(email);
        return ResponseEntity.ok(response);
    }
}