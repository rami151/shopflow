package com.shopflow.order;

import com.shopflow.cart.CartRepository;
import com.shopflow.cart.CouponRepository;
import com.shopflow.order.dto.*;
import com.shopflow.shared.entity.*;
import com.shopflow.shared.enums.OrderStatus;
import com.shopflow.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public OrderResponse createOrder(String userEmail, CreateOrderRequest request) {
        User user = getUserByEmail(userEmail);
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Panier non trouvé"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Le panier est vide");
        }

        Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Adresse non trouvée"));

        for (CartItem item : cart.getItems()) {
            int availableStock = calculateAvailableStock(item.getProduct(), item.getVariant());
            if (item.getQuantite() > availableStock) {
                throw new IllegalArgumentException("Stock insuffisant pour: " + item.getProduct().getNom() + 
                    ". Disponible: " + availableStock);
            }
        }

        String numeroCommande = generateNumeroCommande();

        Order order = Order.builder()
                .numeroCommande(numeroCommande)
                .statut(OrderStatus.PENDING)
                .subtotal(cart.getSubtotal())
                .fraisLivraison(cart.getFraisLivraison())
                .totalTTC(cart.getTotalTTC())
                .couponCode(cart.getCouponCode())
                .couponDiscount(cart.getSubtotal().subtract(cart.getTotalTTC()).subtract(cart.getFraisLivraison()))
                .user(user)
                .adresseLivraison(address)
                .items(new java.util.ArrayList<>())
                .build();

        if (cart.getCouponCode() != null) {
            couponRepository.findByCodeAndActifTrue(cart.getCouponCode())
                    .ifPresent(order::setCoupon);
        }

        order = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .variant(cartItem.getVariant())
                    .quantite(cartItem.getQuantite())
                    .prixUnitaire(cartItem.getPrixUnitaire())
                    .build();
            order.getItems().add(orderItem);
            orderItemRepository.save(orderItem);

            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantite());
            if (cartItem.getVariant() != null) {
                ProductVariant variant = cartItem.getVariant();
                variant.setStockSupplementaire(variant.getStockSupplementaire() - cartItem.getQuantite());
            }
        }

        cart.getItems().clear();
        cart.setSubtotal(BigDecimal.ZERO);
        cart.setFraisLivraison(BigDecimal.ZERO);
        cart.setTotalTTC(BigDecimal.ZERO);
        cart.setCouponCode(null);
        cartRepository.save(cart);

        return toResponse(order);
    }

    private int calculateAvailableStock(Product product, ProductVariant variant) {
        int baseStock = product.getStock();
        if (variant != null) {
            return baseStock + variant.getStockSupplementaire();
        }
        return baseStock;
    }

    private String generateNumeroCommande() {
        String prefix = "ORD-" + Year.now().getValue() + "-";
        String suffix;
        do {
            suffix = String.format("%05d", new Random().nextInt(100000));
        } while (orderRepository.findByNumeroCommande(prefix + suffix).isPresent());
        return prefix + suffix;
    }

    @Transactional
    public OrderResponse simulatePayment(String userEmail, Long orderId) {
        User user = getUserByEmail(userEmail);
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (order.getStatut() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("La commande ne peut pas être payée. Statut actuel: " + order.getStatut());
        }

        order.setStatut(OrderStatus.PAID);
        order = orderRepository.save(order);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(String userEmail, Long orderId, OrderStatus newStatus) {
        User user = getUserByEmail(userEmail);
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (!isValidStatusTransition(order.getStatut(), newStatus)) {
            throw new IllegalArgumentException("Transition invalide de " + order.getStatut() + " vers " + newStatus);
        }

        order.setStatut(newStatus);
        order = orderRepository.save(order);
        return toResponse(order);
    }

    private boolean isValidStatusTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case PENDING -> next == OrderStatus.PAID || next == OrderStatus.CANCELLED;
            case PAID -> next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
            case PROCESSING -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    @Transactional
    public OrderResponse cancelOrder(String userEmail, Long orderId) {
        User user = getUserByEmail(userEmail);
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));

        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.PAID) {
            throw new IllegalArgumentException("La commande ne peut pas être annulée. Statut actuel: " + order.getStatut());
        }

        order.setStatut(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantite());
            if (item.getVariant() != null) {
                ProductVariant variant = item.getVariant();
                variant.setStockSupplementaire(variant.getStockSupplementaire() + item.getQuantite());
            }
        }

        if (order.getCoupon() != null) {
            Coupon coupon = order.getCoupon();
            coupon.setUsagesActuels(coupon.getUsagesActuels() - 1);
            couponRepository.save(coupon);
        }

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Order> orders = orderRepository.findByUserIdOrderByDateCommandeDesc(user.getId());
        return orders.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrdersPaged(String userEmail, Pageable pageable) {
        User user = getUserByEmail(userEmail);
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return orders.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String userEmail, Long orderId) {
        User user = getUserByEmail(userEmail);
        Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream().map(this::toAddressResponse).collect(Collectors.toList());
    }

    private User getUserByEmail(String email) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getUser().getEmail().equals(email))
                .map(Order::getUser)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        AddressResponse addressResponse = toAddressResponse(order.getAdresseLivraison());

        return new OrderResponse(
                order.getId(),
                order.getNumeroCommande(),
                order.getStatut(),
                order.getSubtotal(),
                order.getFraisLivraison(),
                order.getTotalTTC(),
                order.getCouponCode(),
                order.getCouponDiscount() != null ? order.getCouponDiscount() : BigDecimal.ZERO,
                order.getDateCommande(),
                addressResponse,
                itemResponses
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        BigDecimal sousTotal = item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite()));
        String variantNom = item.getVariant() != null ? item.getVariant().getNom() : null;
        String variantValeur = item.getVariant() != null ? item.getVariant().getValeur() : null;

        return new OrderItemResponse(
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

    private AddressResponse toAddressResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getRue(),
                address.getVille(),
                address.getCodePostal(),
                address.getPays(),
                address.isPrincipale()
        );
    }
}