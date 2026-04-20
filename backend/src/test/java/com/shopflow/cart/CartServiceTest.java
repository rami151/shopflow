package com.shopflow.cart;

import com.shopflow.auth.UserRepository;
import com.shopflow.cart.dto.*;
import com.shopflow.product.ProductRepository;
import com.shopflow.shared.entity.*;
import com.shopflow.shared.enums.CouponType;
import com.shopflow.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;
    private Product testProduct;
    private Coupon validCoupon;
    private AddItemRequest addItemRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .nom("Doe")
                .prenom("John")
                .email("john.doe@example.com")
                .role(com.shopflow.shared.enums.Role.CUSTOMER)
                .actif(true)
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .subtotal(BigDecimal.ZERO)
                .fraisLivraison(BigDecimal.ZERO)
                .totalTTC(BigDecimal.ZERO)
                .items(new ArrayList<>())
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

        validCoupon = Coupon.builder()
                .id(1L)
                .code("SAVE10")
                .type(CouponType.PERCENT)
                .valeur(new BigDecimal("10"))
                .usagesMax(100)
                .usagesActuels(0)
                .dateExpiration(LocalDate.now().plusDays(30))
                .actif(true)
                .build();

        addItemRequest = new AddItemRequest(1L, null, 2);
    }

    @Nested
    @DisplayName("Get Or Create Cart Tests")
    class GetOrCreateCartTests {

        @Test
        @DisplayName("Should return existing cart")
        void shouldReturnExistingCart() {
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

            CartResponse response = cartService.getOrCreateCart("john.doe@example.com");

            assertNotNull(response);
            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("Should create new cart when not exists")
        void shouldCreateNewCartWhenNotExists() {
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
                Cart c = invocation.getArgument(0);
                c.setId(1L);
                return c;
            });

            CartResponse response = cartService.getOrCreateCart("john.doe@example.com");

            assertNotNull(response);
            verify(cartRepository).save(any(Cart.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.getOrCreateCart("nonexistent@example.com")
            );

            assertTrue(exception.getMessage().contains("Utilisateur"));
        }
    }

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {

        @Test
        @DisplayName("Should add item to cart successfully")
        void shouldAddItemToCartSuccessfully() {
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));
            when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
            when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
                CartItem item = invocation.getArgument(0);
                item.setId(1L);
                return item;
            });
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.addItem("john.doe@example.com", addItemRequest);

            assertNotNull(response);
            verify(cartItemRepository).save(any(CartItem.class));
        }

        @Test
        @DisplayName("Should update quantity when item already in cart")
        void shouldUpdateQuantityWhenItemAlreadyInCart() {
            CartItem existingItem = CartItem.builder()
                    .id(1L)
                    .cart(testCart)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));
            when(cartItemRepository.findByCartIdAndProductId(1L, 1L)).thenReturn(Optional.of(existingItem));
            when(cartItemRepository.save(existingItem)).thenReturn(existingItem);
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.addItem("john.doe@example.com", addItemRequest);

            assertNotNull(response);
            assertEquals(4, existingItem.getQuantite());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when stock is insufficient")
        void shouldThrowIllegalArgumentExceptionWhenStockIsInsufficient() {
            AddItemRequest largeQuantityRequest = new AddItemRequest(1L, null, 15);

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cartService.addItem("john.doe@example.com", largeQuantityRequest)
            );

            assertTrue(exception.getMessage().contains("Stock insuffisant"));
        }

        @Test
        @DisplayName("Should add item with variant successfully")
        void shouldAddItemWithVariantSuccessfully() {
            ProductVariant variant = ProductVariant.builder()
                    .id(1L)
                    .product(testProduct)
                    .nom("Color")
                    .valeur("Red")
                    .prixSupplementaire(new BigDecimal("5.00"))
                    .stockSupplementaire(5)
                    .actif(true)
                    .build();

            testProduct.setVariants(List.of(variant));

            AddItemRequest variantRequest = new AddItemRequest(1L, 1L, 2);

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));
            when(cartItemRepository.findByCartIdAndProductIdAndVariantId(anyLong(), anyLong(), anyLong()))
                    .thenReturn(Optional.empty());
            when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> {
                CartItem item = invocation.getArgument(0);
                item.setId(1L);
                return item;
            });
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.addItem("john.doe@example.com", variantRequest);

            assertNotNull(response);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.addItem("john.doe@example.com", new AddItemRequest(999L, null, 1))
            );

            assertTrue(exception.getMessage().contains("Produit"));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when variant not found")
        void shouldThrowResourceNotFoundExceptionWhenVariantNotFound() {
            ProductVariant variant = ProductVariant.builder()
                    .id(1L)
                    .product(testProduct)
                    .nom("Color")
                    .valeur("Red")
                    .actif(true)
                    .build();
            testProduct.setVariants(List.of(variant));

            AddItemRequest variantRequest = new AddItemRequest(1L, 999L, 2);

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.addItem("john.doe@example.com", variantRequest)
            );

            assertTrue(exception.getMessage().contains("Variante"));
        }
    }

    @Nested
    @DisplayName("Update Item Quantity Tests")
    class UpdateItemQuantityTests {

        @Test
        @DisplayName("Should update item quantity successfully")
        void shouldUpdateItemQuantitySuccessfully() {
            CartItem existingItem = CartItem.builder()
                    .id(1L)
                    .cart(testCart)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();
            testCart.setItems(List.of(existingItem));

            UpdateItemRequest updateRequest = new UpdateItemRequest(5);

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(cartItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
            when(cartItemRepository.save(existingItem)).thenReturn(existingItem);
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.updateItemQuantity("john.doe@example.com", 1L, updateRequest);

            assertNotNull(response);
            assertEquals(5, existingItem.getQuantite());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when item belongs to different cart")
        void shouldThrowIllegalArgumentExceptionWhenItemBelongsToDifferentCart() {
            Cart otherCart = Cart.builder()
                    .id(2L)
                    .user(testUser)
                    .items(new ArrayList<>())
                    .build();

            CartItem existingItem = CartItem.builder()
                    .id(1L)
                    .cart(otherCart)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();

            UpdateItemRequest updateRequest = new UpdateItemRequest(5);

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(cartItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cartService.updateItemQuantity("john.doe@example.com", 1L, updateRequest)
            );

            assertTrue(exception.getMessage().contains("n'appartient pas"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when stock is insufficient for update")
        void shouldThrowIllegalArgumentExceptionWhenStockIsInsufficientForUpdate() {
            CartItem existingItem = CartItem.builder()
                    .id(1L)
                    .cart(testCart)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();
            testCart.setItems(List.of(existingItem));

            UpdateItemRequest updateRequest = new UpdateItemRequest(15);

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(cartItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cartService.updateItemQuantity("john.doe@example.com", 1L, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Stock insuffisant"));
        }
    }

    @Nested
    @DisplayName("Remove Item Tests")
    class RemoveItemTests {

        @Test
        @DisplayName("Should remove item successfully")
        void shouldRemoveItemSuccessfully() {
            CartItem existingItem = CartItem.builder()
                    .id(1L)
                    .cart(testCart)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("29.99"))
                    .build();
            testCart.setItems(new ArrayList<>(List.of(existingItem)));

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(cartItemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.removeItem("john.doe@example.com", 1L);

            assertNotNull(response);
            verify(cartItemRepository).delete(existingItem);
            assertTrue(testCart.getItems().isEmpty());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when item not found")
        void shouldThrowResourceNotFoundExceptionWhenItemNotFound() {
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(cartItemRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.removeItem("john.doe@example.com", 999L)
            );

            assertTrue(exception.getMessage().contains("Article"));
        }
    }

    @Nested
    @DisplayName("Apply Coupon Tests")
    class ApplyCouponTests {

        @Test
        @DisplayName("Should apply valid percentage coupon")
        void shouldApplyValidPercentageCoupon() {
            testCart.setSubtotal(new BigDecimal("100.00"));
            testCart.setItems(List.of(CartItem.builder()
                    .id(1L)
                    .cart(testCart)
                    .product(testProduct)
                    .quantite(3)
                    .prixUnitaire(new BigDecimal("33.33"))
                    .build()));

            ApplyCouponRequest couponRequest = new ApplyCouponRequest("SAVE10");

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(couponRepository.findByCodeAndActifTrue("SAVE10")).thenReturn(Optional.of(validCoupon));
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.applyCoupon("john.doe@example.com", couponRequest);

            assertNotNull(response);
            assertEquals("SAVE10", testCart.getCouponCode());
        }

        @Test
        @DisplayName("Should apply fixed amount coupon")
        void shouldApplyFixedAmountCoupon() {
            Coupon fixedCoupon = Coupon.builder()
                    .id(2L)
                    .code("FLAT5")
                    .type(CouponType.FIXED)
                    .valeur(new BigDecimal("5"))
                    .usagesMax(100)
                    .usagesActuels(0)
                    .dateExpiration(LocalDate.now().plusDays(30))
                    .actif(true)
                    .build();

            testCart.setSubtotal(new BigDecimal("50.00"));
            testCart.setItems(List.of(CartItem.builder()
                    .id(1L)
                    .cart(testCart)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("25.00"))
                    .build()));

            ApplyCouponRequest couponRequest = new ApplyCouponRequest("FLAT5");

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(couponRepository.findByCodeAndActifTrue("FLAT5")).thenReturn(Optional.of(fixedCoupon));
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.applyCoupon("john.doe@example.com", couponRequest);

            assertNotNull(response);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for invalid coupon")
        void shouldThrowResourceNotFoundExceptionForInvalidCoupon() {
            ApplyCouponRequest couponRequest = new ApplyCouponRequest("INVALID");

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(couponRepository.findByCodeAndActifTrue("INVALID")).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> cartService.applyCoupon("john.doe@example.com", couponRequest)
            );

            assertTrue(exception.getMessage().contains("Code promo"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when coupon max usage reached")
        void shouldThrowIllegalArgumentExceptionWhenCouponMaxUsageReached() {
            validCoupon.setUsagesActuels(100);

            ApplyCouponRequest couponRequest = new ApplyCouponRequest("SAVE10");

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(couponRepository.findByCodeAndActifTrue("SAVE10")).thenReturn(Optional.of(validCoupon));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cartService.applyCoupon("john.doe@example.com", couponRequest)
            );

            assertTrue(exception.getMessage().contains("limite d'utilisation"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when coupon is expired")
        void shouldThrowIllegalArgumentExceptionWhenCouponIsExpired() {
            validCoupon.setDateExpiration(LocalDate.now().minusDays(1));

            ApplyCouponRequest couponRequest = new ApplyCouponRequest("SAVE10");

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(couponRepository.findByCodeAndActifTrue("SAVE10")).thenReturn(Optional.of(validCoupon));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> cartService.applyCoupon("john.doe@example.com", couponRequest)
            );

            assertTrue(exception.getMessage().contains("expiré"));
        }
    }

    @Nested
    @DisplayName("Remove Coupon Tests")
    class RemoveCouponTests {

        @Test
        @DisplayName("Should remove coupon from cart")
        void shouldRemoveCouponFromCart() {
            testCart.setCouponCode("SAVE10");

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            CartResponse response = cartService.removeCoupon("john.doe@example.com");

            assertNotNull(response);
            assertNull(testCart.getCouponCode());
        }
    }

    @Nested
    @DisplayName("Shipping Cost Tests")
    class ShippingCostTests {

        @Test
        @DisplayName("Should calculate free shipping for orders above 50")
        void shouldCalculateFreeShippingForOrdersAbove50() {
            CartItem item = CartItem.builder()
                    .id(1L)
                    .cart(testCart)
                    .product(testProduct)
                    .quantite(2)
                    .prixUnitaire(new BigDecimal("30.00"))
                    .build();
            testCart.setItems(new ArrayList<>(List.of(item)));

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));
            when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
            when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            AddItemRequest request = new AddItemRequest(1L, null, 2);
            CartResponse response = cartService.addItem("john.doe@example.com", request);

            assertEquals(BigDecimal.ZERO, testCart.getFraisLivraison());
        }

        @Test
        @DisplayName("Should calculate shipping cost for orders below 50")
        void shouldCalculateShippingCostForOrdersBelow50() {
            testProduct.setPrix(new BigDecimal("20.00"));

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));
            when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
            when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

            AddItemRequest request = new AddItemRequest(1L, null, 2);
            cartService.addItem("john.doe@example.com", request);

            assertEquals(new BigDecimal("5.90"), testCart.getFraisLivraison());
        }
    }
}
