package com.shopflow.product;

import com.shopflow.auth.UserRepository;
import com.shopflow.shared.entity.Category;
import com.shopflow.shared.entity.Product;
import com.shopflow.shared.entity.ProductVariant;
import com.shopflow.shared.entity.SellerProfile;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.enums.Role;
import com.shopflow.shared.exception.ResourceNotFoundException;
import com.shopflow.shared.exception.UnauthorizedException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private User sellerUser;
    private SellerProfile sellerProfile;
    private Product testProduct;
    private Category testCategory;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

        sellerUser = User.builder()
                .id(1L)
                .nom("Seller")
                .prenom("John")
                .email("seller@example.com")
                .role(Role.SELLER)
                .actif(true)
                .build();

        sellerProfile = SellerProfile.builder()
                .id(1L)
                .user(sellerUser)
                .nomBoutique("Test Shop")
                .actif(true)
                .build();

        sellerUser.setSellerProfile(sellerProfile);

        testCategory = Category.builder()
                .id(1L)
                .nom("Electronics")
                .actif(true)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .nom("Test Product")
                .description("Test Description")
                .prix(new BigDecimal("99.99"))
                .stock(10)
                .actif(true)
                .sellerProfile(sellerProfile)
                .categories(new ArrayList<>())
                .variants(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("Get All Products Tests")
    class GetAllProductsTests {

        @Test
        @DisplayName("Should return active products")
        void shouldReturnActiveProducts() {
            Page<Product> expectedPage = new PageImpl<>(List.of(testProduct));
            when(productRepository.findByActifTrue(pageable)).thenReturn(expectedPage);

            Page<Product> result = productService.getAllProducts(pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Test Product", result.getContent().get(0).getNom());
        }

        @Test
        @DisplayName("Should return empty page when no products")
        void shouldReturnEmptyPageWhenNoProducts() {
            Page<Product> emptyPage = new PageImpl<>(List.of());
            when(productRepository.findByActifTrue(pageable)).thenReturn(emptyPage);

            Page<Product> result = productService.getAllProducts(pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Get Products With Filters Tests")
    class GetProductsWithFiltersTests {

        @Test
        @DisplayName("Should filter by category when categoryId provided")
        void shouldFilterByCategoryWhenCategoryIdProvided() {
            Page<Product> expectedPage = new PageImpl<>(List.of(testProduct));
            when(productRepository.findByCategoryId(1L, pageable)).thenReturn(expectedPage);

            Page<Product> result = productService.getProductsWithFilters(1L, null, null, null, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(productRepository).findByCategoryId(1L, pageable);
        }

        @Test
        @DisplayName("Should filter by price range when min and max price provided")
        void shouldFilterByPriceRangeWhenMinAndMaxPriceProvided() {
            Page<Product> expectedPage = new PageImpl<>(List.of(testProduct));
            when(productRepository.findByPriceRange(any(), any(), any())).thenReturn(expectedPage);

            Page<Product> result = productService.getProductsWithFilters(null, 
                    new BigDecimal("50"), new BigDecimal("150"), null, pageable);

            assertNotNull(result);
            verify(productRepository).findByPriceRange(any(), any(), any());
        }

        @Test
        @DisplayName("Should filter by seller when sellerId provided")
        void shouldFilterBySellerWhenSellerIdProvided() {
            Page<Product> expectedPage = new PageImpl<>(List.of(testProduct));
            when(productRepository.findByActifTrueAndSellerProfileId(1L, pageable)).thenReturn(expectedPage);

            Page<Product> result = productService.getProductsWithFilters(null, null, null, 1L, pageable);

            assertNotNull(result);
            verify(productRepository).findByActifTrueAndSellerProfileId(1L, pageable);
        }

        @Test
        @DisplayName("Should return all active products when no filters")
        void shouldReturnAllActiveProductsWhenNoFilters() {
            Page<Product> expectedPage = new PageImpl<>(List.of(testProduct));
            when(productRepository.findByActifTrue(pageable)).thenReturn(expectedPage);

            Page<Product> result = productService.getProductsWithFilters(null, null, null, null, pageable);

            assertNotNull(result);
            verify(productRepository).findByActifTrue(pageable);
        }
    }

    @Nested
    @DisplayName("Get Product By Id Tests")
    class GetProductByIdTests {

        @Test
        @DisplayName("Should return product when found")
        void shouldReturnProductWhenFound() {
            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));

            Product result = productService.getProductById(1L);

            assertNotNull(result);
            assertEquals("Test Product", result.getNom());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
            when(productRepository.findByIdAndActifTrue(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.getProductById(999L)
            );

            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("Search Products Tests")
    class SearchProductsTests {

        @Test
        @DisplayName("Should search products by keyword")
        void shouldSearchProductsByKeyword() {
            Page<Product> expectedPage = new PageImpl<>(List.of(testProduct));
            when(productRepository.searchByKeyword("test", pageable)).thenReturn(expectedPage);

            Page<Product> result = productService.searchProducts("test", pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Should return empty results for non-matching keyword")
        void shouldReturnEmptyResultsForNonMatchingKeyword() {
            Page<Product> emptyPage = new PageImpl<>(List.of());
            when(productRepository.searchByKeyword("nonexistent", pageable)).thenReturn(emptyPage);

            Page<Product> result = productService.searchProducts("nonexistent", pageable);

            assertNotNull(result);
            assertEquals(0, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully for seller")
        void shouldCreateProductSuccessfullyForSeller() {
            Product newProduct = Product.builder()
                    .nom("New Product")
                    .prix(new BigDecimal("49.99"))
                    .stock(5)
                    .build();

            when(userRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(sellerUser));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                p.setId(2L);
                return p;
            });

            Product result = productService.createProduct(newProduct, null, "seller@example.com");

            assertNotNull(result);
            assertEquals("New Product", result.getNom());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should create product with categories")
        void shouldCreateProductWithCategories() {
            Product newProduct = Product.builder()
                    .nom("New Product")
                    .prix(new BigDecimal("49.99"))
                    .stock(5)
                    .build();

            when(userRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(sellerUser));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                p.setId(2L);
                return p;
            });

            Product result = productService.createProduct(newProduct, Set.of(1L), "seller@example.com");

            assertNotNull(result);
            assertFalse(result.getCategories().isEmpty());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user not found")
        void shouldThrowResourceNotFoundExceptionWhenUserNotFound() {
            Product newProduct = Product.builder()
                    .nom("New Product")
                    .build();

            when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.createProduct(newProduct, null, "nonexistent@example.com")
            );

            assertTrue(exception.getMessage().contains("Utilisateur"));
        }

        @Test
        @DisplayName("Should throw UnauthorizedException when user is not a seller")
        void shouldThrowUnauthorizedExceptionWhenUserIsNotSeller() {
            User customerUser = User.builder()
                    .id(2L)
                    .nom("Customer")
                    .prenom("Jane")
                    .email("customer@example.com")
                    .role(Role.CUSTOMER)
                    .actif(true)
                    .build();

            Product newProduct = Product.builder()
                    .nom("New Product")
                    .build();

            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customerUser));

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> productService.createProduct(newProduct, null, "customer@example.com")
            );

            assertTrue(exception.getMessage().contains("non autorisé"));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when category not found")
        void shouldThrowResourceNotFoundExceptionWhenCategoryNotFound() {
            Product newProduct = Product.builder()
                    .nom("New Product")
                    .build();

            when(userRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(sellerUser));
            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.createProduct(newProduct, Set.of(999L), "seller@example.com")
            );

            assertTrue(exception.getMessage().contains("Catégorie"));
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            Product updatedData = Product.builder()
                    .nom("Updated Name")
                    .description("Updated Description")
                    .prix(new BigDecimal("79.99"))
                    .stock(20)
                    .build();

            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            Product result = productService.updateProduct(1L, updatedData, null, "seller@example.com");

            assertNotNull(result);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw UnauthorizedException when not the owner")
        void shouldThrowUnauthorizedExceptionWhenNotTheOwner() {
            Product updatedData = Product.builder()
                    .nom("Updated Name")
                    .build();

            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> productService.updateProduct(1L, updatedData, null, "other@example.com")
            );

            assertTrue(exception.getMessage().contains("pas autorisé"));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product not found")
        void shouldThrowResourceNotFoundExceptionWhenProductNotFound() {
            Product updatedData = Product.builder()
                    .nom("Updated Name")
                    .build();

            when(productRepository.findByIdAndActifTrue(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.updateProduct(999L, updatedData, null, "seller@example.com")
            );

            assertTrue(exception.getMessage().contains("non trouvé"));
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product successfully (soft delete)")
        void shouldDeleteProductSuccessfully() {
            ProductVariant variant = ProductVariant.builder()
                    .id(1L)
                    .product(testProduct)
                    .actif(true)
                    .build();
            testProduct.setVariants(List.of(variant));

            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            assertDoesNotThrow(() -> productService.deleteProduct(1L, "seller@example.com"));

            assertFalse(testProduct.isActif());
            assertFalse(variant.isActif());
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should throw UnauthorizedException when not the owner")
        void shouldThrowUnauthorizedExceptionWhenNotTheOwner() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> productService.deleteProduct(1L, "other@example.com")
            );

            assertTrue(exception.getMessage().contains("pas autorisé"));
        }
    }

    @Nested
    @DisplayName("Variant Tests")
    class VariantTests {

        @Test
        @DisplayName("Should add variant successfully")
        void shouldAddVariantSuccessfully() {
            ProductVariant newVariant = ProductVariant.builder()
                    .nom("Color")
                    .valeur("Red")
                    .prixSupplementaire(new BigDecimal("10.00"))
                    .stockSupplementaire(5)
                    .build();

            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));
            when(productVariantRepository.save(any(ProductVariant.class))).thenAnswer(invocation -> {
                ProductVariant v = invocation.getArgument(0);
                v.setId(1L);
                return v;
            });

            ProductVariant result = productService.addVariant(1L, newVariant, "seller@example.com");

            assertNotNull(result);
            assertTrue(result.isActif());
            verify(productVariantRepository).save(any(ProductVariant.class));
        }

        @Test
        @DisplayName("Should delete variant successfully")
        void shouldDeleteVariantSuccessfully() {
            ProductVariant variant = ProductVariant.builder()
                    .id(1L)
                    .product(testProduct)
                    .actif(true)
                    .build();

            when(productVariantRepository.findById(1L)).thenReturn(Optional.of(variant));
            when(productVariantRepository.save(any(ProductVariant.class))).thenReturn(variant);

            assertDoesNotThrow(() -> productService.deleteVariant(1L, "seller@example.com"));

            assertFalse(variant.isActif());
            verify(productVariantRepository).save(variant);
        }

        @Test
        @DisplayName("Should throw UnauthorizedException when adding variant by non-owner")
        void shouldThrowUnauthorizedExceptionWhenAddingVariantByNonOwner() {
            ProductVariant newVariant = ProductVariant.builder()
                    .nom("Color")
                    .valeur("Red")
                    .build();

            when(productRepository.findByIdAndActifTrue(1L)).thenReturn(Optional.of(testProduct));

            UnauthorizedException exception = assertThrows(
                    UnauthorizedException.class,
                    () -> productService.addVariant(1L, newVariant, "other@example.com")
            );

            assertTrue(exception.getMessage().contains("pas autorisé"));
        }
    }
}
