package com.shopflow.order;

import com.shopflow.cart.CartRepository;
import com.shopflow.cart.CouponRepository;
import com.shopflow.order.dto.*;
import com.shopflow.shared.entity.*;
import com.shopflow.shared.enums.CouponType;
import com.shopflow.shared.enums.OrderStatus;
import com.shopflow.shared.enums.Role;
import com.shopflow.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;
    private Address testAddress;
    private Order testOrder;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        testUser = User.builder()
                .id(1L)
                .nom("Doe")
                .prenom("John")
                .email("john.doe@example.com")
                .role(Role.CUSTOMER)
                .actif(true)
                .build();

        testAddress = Address.builder()
                .id(1L)
                .rue("123 Main St")
                .ville("Paris")
                .codePostal("75001")
                .pays("France")
                .principale(true)
                .user(testUser)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .nom("Test Product")
                .prix(new BigDecimal("29.99"))
                .stock(10)
                .actif(true)
                .categories(new ArrayList<>())
                .variants(new ArrayList<>())
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(testProduct)
                .quantite(2)
                .prixUnitaire(new BigDecimal("29.99"))
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .subtotal(new BigDecimal("59.98"))
                .fraisLivraison(BigDecimal.ZERO)
                .totalTTC(new BigDecimal("59.98"))
                .items(new ArrayList<>(List.of(cartItem)))
                .build();

        cartItem.setCart(testCart);

        testOrder = Order.builder()
                .id(1L)
                .numeroCommande("ORD-2026-00001")
                .statut(OrderStatus.PENDING)
                .subtotal(new BigDecimal("59.98"))
                .fraisLivraison(BigDecimal.ZERO)
                .totalTTC(new BigDecimal("59.98"))
                .couponCode(null)
                .couponDiscount(BigDecimal.ZERO)
                .user(testUser)
                .adresseLivraison(testAddress)
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findAll()).thenReturn(new ArrayList<>());
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrderSuccessfully() {
            CreateOrderRequest request = new CreateOrderRequest(1L);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));
            when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
                Order o = invocation.getArgument(0);
                o.setId(1L);
                return o;
            });
            when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            OrderResponse response = orderService.createOrder("john.doe@example.com", request);

            assertNotNull(response);
            assertEquals(OrderStatus.PENDING, response.statut());
            assertTrue(testProduct.getStock() < 10);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when cart not found")
        void shouldThrowResourceNotFoundExceptionWhenCartNotFound() {
            CreateOrderRequest request = new CreateOrderRequest(1L);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> orderService.createOrder("john.doe@example.com", request)
            );

            assertTrue(exception.getMessage().contains("Panier"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when cart is empty")
        void shouldThrowIllegalArgumentExceptionWhenCartIsEmpty() {
            testCart.setItems(new ArrayList<>());

            CreateOrderRequest request = new CreateOrderRequest(1L);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderService.createOrder("john.doe@example.com", request)
            );

            assertTrue(exception.getMessage().contains("vide"));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when address not found")
        void shouldThrowResourceNotFoundExceptionWhenAddressNotFound() {
            CreateOrderRequest request = new CreateOrderRequest(999L);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(addressRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> orderService.createOrder("john.doe@example.com", request)
            );

            assertTrue(exception.getMessage().contains("Adresse"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when stock is insufficient")
        void shouldThrowIllegalArgumentExceptionWhenStockIsInsufficient() {
            testProduct.setStock(1);

            CreateOrderRequest request = new CreateOrderRequest(1L);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAddress));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderService.createOrder("john.doe@example.com", request)
            );

            assertTrue(exception.getMessage().contains("Stock insuffisant"));
        }
    }

    @Nested
    @DisplayName("Simulate Payment Tests")
    class SimulatePaymentTests {

        @Test
        @DisplayName("Should simulate payment successfully for pending order")
        void shouldSimulatePaymentSuccessfullyForPendingOrder() {
            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            OrderResponse response = orderService.simulatePayment("john.doe@example.com", 1L);

            assertNotNull(response);
            assertEquals(OrderStatus.PAID, testOrder.getStatut());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when order not pending")
        void shouldThrowIllegalArgumentExceptionWhenOrderNotPending() {
            testOrder.setStatut(OrderStatus.SHIPPED);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderService.simulatePayment("john.doe@example.com", 1L)
            );

            assertTrue(exception.getMessage().contains("ne peut pas être payée"));
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update status from PENDING to PAID")
        void shouldUpdateStatusFromPendingToPaid() {
            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            OrderResponse response = orderService.updateStatus("john.doe@example.com", 1L, OrderStatus.PAID);

            assertNotNull(response);
            assertEquals(OrderStatus.PAID, testOrder.getStatut());
        }

        @Test
        @DisplayName("Should update status from PAID to PROCESSING")
        void shouldUpdateStatusFromPaidToProcessing() {
            testOrder.setStatut(OrderStatus.PAID);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            OrderResponse response = orderService.updateStatus("john.doe@example.com", 1L, OrderStatus.PROCESSING);

            assertNotNull(response);
            assertEquals(OrderStatus.PROCESSING, testOrder.getStatut());
        }

        @Test
        @DisplayName("Should update status from PROCESSING to SHIPPED")
        void shouldUpdateStatusFromProcessingToShipped() {
            testOrder.setStatut(OrderStatus.PROCESSING);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            OrderResponse response = orderService.updateStatus("john.doe@example.com", 1L, OrderStatus.SHIPPED);

            assertNotNull(response);
            assertEquals(OrderStatus.SHIPPED, testOrder.getStatut());
        }

        @Test
        @DisplayName("Should update status from SHIPPED to DELIVERED")
        void shouldUpdateStatusFromShippedToDelivered() {
            testOrder.setStatut(OrderStatus.SHIPPED);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            OrderResponse response = orderService.updateStatus("john.doe@example.com", 1L, OrderStatus.DELIVERED);

            assertNotNull(response);
            assertEquals(OrderStatus.DELIVERED, testOrder.getStatut());
        }

        @Test
        @DisplayName("Should throw for invalid transition from PENDING to SHIPPED")
        void shouldThrowForInvalidTransitionFromPendingToShipped() {
            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderService.updateStatus("john.doe@example.com", 1L, OrderStatus.SHIPPED)
            );

            assertTrue(exception.getMessage().contains("Transition invalide"));
        }

        @Test
        @DisplayName("Should throw for invalid transition from CANCELLED")
        void shouldThrowForInvalidTransitionFromCancelled() {
            testOrder.setStatut(OrderStatus.CANCELLED);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderService.updateStatus("john.doe@example.com", 1L, OrderStatus.PAID)
            );

            assertTrue(exception.getMessage().contains("Transition invalide"));
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel pending order and restore stock")
        void shouldCancelPendingOrderAndRestoreStock() {
            testProduct.setStock(10);

            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();
            List<OrderItem> items = new ArrayList<>();
            items.add(orderItem);
            testOrder.setItems(items);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            OrderResponse response = orderService.cancelOrder("john.doe@example.com", 1L);

            assertNotNull(response);
            assertEquals(OrderStatus.CANCELLED, testOrder.getStatut());
            assertEquals(12, testProduct.getStock());
        }

        @Test
        @DisplayName("Should cancel paid order and restore stock")
        void shouldCancelPaidOrderAndRestoreStock() {
            testOrder.setStatut(OrderStatus.PAID);
            testProduct.setStock(10);

            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();
            List<OrderItem> items = new ArrayList<>();
            items.add(orderItem);
            testOrder.setItems(items);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            OrderResponse response = orderService.cancelOrder("john.doe@example.com", 1L);

            assertNotNull(response);
            assertEquals(OrderStatus.CANCELLED, testOrder.getStatut());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when trying to cancel shipped order")
        void shouldThrowIllegalArgumentExceptionWhenTryingToCancelShippedOrder() {
            testOrder.setStatut(OrderStatus.SHIPPED);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderService.cancelOrder("john.doe@example.com", 1L)
            );

            assertTrue(exception.getMessage().contains("ne peut pas être annulée"));
        }

        @Test
        @DisplayName("Should restore variant stock on cancellation")
        void shouldRestoreVariantStockOnCancellation() {
            testOrder.setStatut(OrderStatus.PAID);

            ProductVariant variant = ProductVariant.builder()
                    .id(1L)
                    .stockSupplementaire(3)
                    .build();

            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .product(testProduct)
                    .variant(variant)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();
            List<OrderItem> items = new ArrayList<>();
            items.add(orderItem);
            testOrder.setItems(items);
            testProduct.setStock(8);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            orderService.cancelOrder("john.doe@example.com", 1L);

            assertEquals(5, variant.getStockSupplementaire());
        }

        @Test
        @DisplayName("Should decrement coupon usage on cancellation")
        void shouldDecrementCouponUsageOnCancellation() {
            testOrder.setStatut(OrderStatus.PAID);

            Coupon coupon = Coupon.builder()
                    .id(1L)
                    .code("SAVE10")
                    .type(CouponType.PERCENT)
                    .valeur(new BigDecimal("10"))
                    .usagesMax(100)
                    .usagesActuels(5)
                    .dateExpiration(LocalDate.now().plusDays(30))
                    .actif(true)
                    .build();

            testOrder.setCoupon(coupon);
            testOrder.setCouponCode("SAVE10");

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

            orderService.cancelOrder("john.doe@example.com", 1L);

            assertEquals(4, coupon.getUsagesActuels());
        }
    }

    @Nested
    @DisplayName("Get User Orders Tests")
    class GetUserOrdersTests {

        @Test
        @DisplayName("Should get user orders")
        void shouldGetUserOrders() {
            List<Order> orders = List.of(testOrder);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByUserIdOrderByDateCommandeDesc(1L)).thenReturn(orders);

            List<OrderResponse> responses = orderService.getUserOrders("john.doe@example.com");

            assertNotNull(responses);
            assertEquals(1, responses.size());
        }

        @Test
        @DisplayName("Should return empty list when no orders")
        void shouldReturnEmptyListWhenNoOrders() {
            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByUserIdOrderByDateCommandeDesc(1L)).thenReturn(new ArrayList<>());

            List<OrderResponse> responses = orderService.getUserOrders("john.doe@example.com");

            assertNotNull(responses);
            assertTrue(responses.isEmpty());
        }

        @Test
        @DisplayName("Should get user orders paged")
        void shouldGetUserOrdersPaged() {
            Page<Order> orderPage = new PageImpl<>(List.of(testOrder));

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByUser(testUser, pageable)).thenReturn(orderPage);

            Page<OrderResponse> response = orderService.getUserOrdersPaged("john.doe@example.com", pageable);

            assertNotNull(response);
            assertEquals(1, response.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Get Order By Id Tests")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Should get order by id")
        void shouldGetOrderById() {
            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testOrder));

            OrderResponse response = orderService.getOrderById("john.doe@example.com", 1L);

            assertNotNull(response);
            assertEquals("ORD-2026-00001", response.numeroCommande());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when order not found")
        void shouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(orderRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> orderService.getOrderById("john.doe@example.com", 999L)
            );

            assertTrue(exception.getMessage().contains("Commande"));
        }
    }

    @Nested
    @DisplayName("Get User Addresses Tests")
    class GetUserAddressesTests {

        @Test
        @DisplayName("Should get user addresses")
        void shouldGetUserAddresses() {
            List<Address> addresses = List.of(testAddress);

            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(addressRepository.findByUser(testUser)).thenReturn(addresses);

            List<AddressResponse> responses = orderService.getUserAddresses("john.doe@example.com");

            assertNotNull(responses);
            assertEquals(1, responses.size());
        }

        @Test
        @DisplayName("Should return empty list when no addresses")
        void shouldReturnEmptyListWhenNoAddresses() {
            Order orderWithUser = Order.builder().user(testUser).build();
            when(orderRepository.findAll()).thenReturn(List.of(orderWithUser));
            when(addressRepository.findByUser(testUser)).thenReturn(new ArrayList<>());

            List<AddressResponse> responses = orderService.getUserAddresses("john.doe@example.com");

            assertNotNull(responses);
            assertTrue(responses.isEmpty());
        }
    }
}