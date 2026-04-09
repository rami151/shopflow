package com.shopflow.cart;

import com.shopflow.auth.UserRepository;
import com.shopflow.cart.dto.*;
import com.shopflow.product.ProductRepository;
import com.shopflow.shared.entity.*;
import com.shopflow.shared.enums.CouponType;
import com.shopflow.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse getOrCreateCart(String userEmail) {
        Cart cart = getOrCreateCartEntity(userEmail);
        return toResponse(cart);
    }

    @Transactional
    public Cart getOrCreateCartEntity(String userEmail) {
        User user = getUserByEmail(userEmail);
        return cartRepository.findByUser(user)
                .orElseGet(() -> createCart(user));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }

    private Cart createCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .subtotal(BigDecimal.ZERO)
                .fraisLivraison(BigDecimal.ZERO)
                .totalTTC(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();
        return cartRepository.save(cart);
    }

    @Transactional
    public CartResponse addItem(String userEmail, AddItemRequest request) {
        Cart cart = getOrCreateCartEntity(userEmail);
        Product product = productRepository.findByIdAndActifTrue(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + request.productId()));

        ProductVariant variant = null;
        if (request.variantId() != null) {
            variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(request.variantId()) && v.isActif())
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Variante non trouvée avec l'ID: " + request.variantId()));
        }

        int availableStock = calculateAvailableStock(product, variant);
        if (request.quantite() > availableStock) {
            throw new IllegalArgumentException("Stock insuffisant. Disponible: " + availableStock);
        }

        BigDecimal unitPrice = calculateUnitPrice(product, variant);

        CartItem cartItem;
        if (variant != null) {
            cartItem = cartItemRepository.findByCartIdAndProductIdAndVariantId(cart.getId(), product.getId(), variant.getId())
                    .orElse(null);
        } else {
            cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                    .filter(item -> item.getVariant() == null)
                    .orElse(null);
        }

        if (cartItem != null) {
            int newQuantity = cartItem.getQuantite() + request.quantite();
            if (newQuantity > availableStock) {
                throw new IllegalArgumentException("Stock insuffisant pour cette quantité. Disponible: " + availableStock);
            }
            cartItem.setQuantite(newQuantity);
            cartItem.setPrixUnitaire(unitPrice);
            cartItem = cartItemRepository.save(cartItem);
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantite(request.quantite())
                    .prixUnitaire(unitPrice)
                    .build();
            cart.getItems().add(cartItem);
            cartItem = cartItemRepository.save(cartItem);
        }

        recalculateCartTotals(cart);
        return toResponse(cart);
    }

    private int calculateAvailableStock(Product product, ProductVariant variant) {
        int baseStock = product.getStock();
        if (variant != null) {
            return baseStock + variant.getStockSupplementaire();
        }
        return baseStock;
    }

    private BigDecimal calculateUnitPrice(Product product, ProductVariant variant) {
        BigDecimal basePrice = product.getPrix();
        if (variant != null) {
            return basePrice.add(variant.getPrixSupplementaire());
        }
        return basePrice;
    }

    @Transactional
    public CartResponse updateItemQuantity(String userEmail, Long itemId, UpdateItemRequest request) {
        Cart cart = getOrCreateCartEntity(userEmail);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'ID: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cet article n'appartient pas à votre panier");
        }

        Product product = cartItem.getProduct();
        ProductVariant variant = cartItem.getVariant();
        int availableStock = calculateAvailableStock(product, variant);

        if (request.quantite() > availableStock) {
            throw new IllegalArgumentException("Stock insuffisant. Disponible: " + availableStock);
        }

        cartItem.setQuantite(request.quantite());
        cartItemRepository.save(cartItem);

        recalculateCartTotals(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String userEmail, Long itemId) {
        Cart cart = getOrCreateCartEntity(userEmail);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé avec l'ID: " + itemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cet article n'appartient pas à votre panier");
        }

        cartItemRepository.delete(cartItem);
        cart.getItems().remove(cartItem);

        recalculateCartTotals(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse applyCoupon(String userEmail, ApplyCouponRequest request) {
        Cart cart = getOrCreateCartEntity(userEmail);

        Coupon coupon = couponRepository.findByCodeAndActifTrue(request.code())
                .orElseThrow(() -> new ResourceNotFoundException("Code promo invalide ou expiré"));

        if (coupon.getUsagesActuels() >= coupon.getUsagesMax()) {
            throw new IllegalArgumentException("Ce code promo a atteint sa limite d'utilisation");
        }

        if (coupon.getDateExpiration().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Ce code promo a expiré");
        }

        cart.setCouponCode(coupon.getCode());
        recalculateCartTotals(cart);
        cartRepository.save(cart);

        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeCoupon(String userEmail) {
        Cart cart = getOrCreateCartEntity(userEmail);
        cart.setCouponCode(null);
        recalculateCartTotals(cart);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    private void recalculateCartTotals(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            subtotal = subtotal.add(item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())));
        }

        BigDecimal fraisLivraison = calculateFraisLivraison(subtotal);

        BigDecimal discount = BigDecimal.ZERO;
        if (cart.getCouponCode() != null) {
            Coupon coupon = couponRepository.findByCodeAndActifTrue(cart.getCouponCode()).orElse(null);
            if (coupon != null) {
                if (coupon.getType() == CouponType.PERCENT) {
                    discount = subtotal.multiply(coupon.getValeur()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else {
                    discount = coupon.getValeur();
                }
            }
        }

        BigDecimal totalTTC = subtotal.add(fraisLivraison).subtract(discount);

        cart.setSubtotal(subtotal);
        cart.setFraisLivraison(fraisLivraison);
        cart.setTotalTTC(totalTTC.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalTTC);

        cartRepository.save(cart);
    }

    private BigDecimal calculateFraisLivraison(BigDecimal subtotal) {
        if (subtotal.compareTo(new BigDecimal("50")) >= 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal("5.90");
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new CartResponse(
                cart.getId(),
                cart.getSubtotal(),
                cart.getFraisLivraison(),
                cart.getTotalTTC(),
                cart.getCouponCode(),
                itemResponses
        );
    }

    private CartItemResponse toItemResponse(CartItem item) {
        BigDecimal sousTotal = item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite()));

        String variantNom = item.getVariant() != null ? item.getVariant().getNom() : null;
        String variantValeur = item.getVariant() != null ? item.getVariant().getValeur() : null;

        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getNom(),
                item.getProduct().getImageUrl(),
                item.getVariant() != null ? item.getVariant().getId() : null,
                variantNom,
                variantValeur,
                item.getPrixUnitaire(),
                item.getQuantite(),
                sousTotal
        );
    }
}