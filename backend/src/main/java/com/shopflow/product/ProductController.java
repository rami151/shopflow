package com.shopflow.product;

import com.shopflow.product.dto.*;
import com.shopflow.shared.entity.Category;
import com.shopflow.shared.entity.Product;
import com.shopflow.shared.entity.ProductVariant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "API de gestion des produits")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Récupérer tous les produits avec filtres", description = "Endpoint public - supporte pagination et filtres optionnels")
    public ResponseEntity<Page<ProductSummaryDto>> getAllProducts(
            @Parameter(description = "Numéro de page (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direction de tri (asc/desc)") @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Filtrer par catégorie") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Prix minimum") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Prix maximum") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Filtrer par vendeur") @RequestParam(required = false) Long sellerId
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productService.getProductsWithFilters(categoryId, minPrice, maxPrice, sellerId, pageable);
        Page<ProductSummaryDto> dtoPage = productPage.map(p -> new ProductSummaryDto(
                p.getId(),
                p.getNom(),
                p.getPrix(),
                p.getImageUrl(),
                p.getStock(),
                p.getSellerProfile().getUser().getNom() + " " + p.getSellerProfile().getUser().getPrenom(),
                calculateAverageRating(p)
        ));
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit par ID", description = "Endpoint public - retourne les détails complets d'un produit")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product p = productService.getProductById(id);
        ProductDto dto = new ProductDto(
                p.getId(),
                p.getNom(),
                p.getDescription(),
                p.getPrix(),
                p.getStock(),
                p.getImageUrl(),
                p.isActif(),
                p.getSellerProfile().getId(),
                p.getSellerProfile().getUser().getNom() + " " + p.getSellerProfile().getUser().getPrenom(),
                convertCategoriesToDtos(p.getCategories()),
                convertVariantsToDtos(p.getVariants()),
                calculateAverageRating(p),
                p.getReviews() != null ? p.getReviews().size() : 0
        );
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des produits", description = "Endpoint public - recherche par mot-clé dans nom et description")
    public ResponseEntity<Page<ProductSummaryDto>> searchProducts(
            @Parameter(description = "Mot-clé de recherche") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.searchProducts(q, pageable);
        Page<ProductSummaryDto> dtoPage = productPage.map(p -> new ProductSummaryDto(
                p.getId(),
                p.getNom(),
                p.getPrix(),
                p.getImageUrl(),
                p.getStock(),
                p.getSellerProfile().getUser().getNom() + " " + p.getSellerProfile().getUser().getPrenom(),
                calculateAverageRating(p)
        ));
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Récupérer les produits les plus vendus", description = "Endpoint public - produits triés par nombre de ventes")
    public ResponseEntity<Page<ProductSummaryDto>> getTopSellingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getTopSellingProducts(pageable);
        Page<ProductSummaryDto> dtoPage = productPage.map(p -> new ProductSummaryDto(
                p.getId(),
                p.getNom(),
                p.getPrix(),
                p.getImageUrl(),
                p.getStock(),
                p.getSellerProfile().getUser().getNom() + " " + p.getSellerProfile().getUser().getPrenom(),
                calculateAverageRating(p)
        ));
        return ResponseEntity.ok(dtoPage);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Créer un nouveau produit", description = "Réservé SELLER et ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);

        Product product = new Product();
        product.setNom(request.nom());
        product.setDescription(request.description());
        product.setPrix(request.prix());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());

        Product saved = productService.createProduct(product, request.categoryIds(), email);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Mettre à jour un produit", description = "Réservé SELLER (ses produits) et ADMIN (tous)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);

        Product product = new Product();
        product.setNom(request.nom());
        product.setDescription(request.description());
        product.setPrix(request.prix());
        product.setStock(request.stock());
        product.setImageUrl(request.imageUrl());

        Product updated = productService.updateProduct(id, product, request.categoryIds(), email);
        return ResponseEntity.ok(convertToDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Supprimer un produit (soft delete)", description = "Réservé SELLER (ses produits) et ADMIN (tous)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        productService.deleteProduct(id, email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/variants")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Ajouter une variante à un produit", description = "Réservé SELLER (ses produits) et ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductVariantDto> addVariant(
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductVariantRequest request,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);

        ProductVariant variant = new ProductVariant();
        variant.setNom(request.couleur());
        variant.setValeur(request.taille());
        variant.setPrixSupplementaire(request.prixSupplementaire());
        variant.setStockSupplementaire(request.stockSupplementaire());

        ProductVariant saved = productService.addVariant(productId, variant, email);
        ProductVariantDto dto = new ProductVariantDto(
                saved.getId(),
                saved.getNom(),
                saved.getValeur(),
                saved.getPrixSupplementaire(),
                saved.getStockSupplementaire(),
                saved.isActif()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/variants/{variantId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Supprimer une variante (soft delete)", description = "Réservé SELLER (ses produits) et ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long variantId,
            Authentication authentication
    ) {
        String email = getAuthenticatedEmail(authentication);
        productService.deleteVariant(variantId, email);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedEmail(Authentication authentication) {
        return authentication.getName();
    }

    private Double calculateAverageRating(Product product) {
        if (product.getReviews() == null || product.getReviews().isEmpty()) {
            return null;
        }
        return product.getReviews().stream()
                .mapToInt(r -> r.getNote())
                .average()
                .orElse(0.0);
    }

    private Set<CategoryDto> convertCategoriesToDtos(List<Category> categories) {
        if (categories == null) return Set.of();
        return categories.stream()
                .map(c -> new CategoryDto(
                        c.getId(),
                        c.getNom(),
                        c.getDescription(),
                        c.getParent() != null ? c.getParent().getId() : null,
                        c.getParent() != null ? c.getParent().getNom() : null,
                        c.isActif()
                ))
                .collect(Collectors.toSet());
    }

    private List<ProductVariantDto> convertVariantsToDtos(List<ProductVariant> variants) {
        if (variants == null) return List.of();
        return variants.stream()
                .filter(v -> v.isActif())
                .map(v -> new ProductVariantDto(
                        v.getId(),
                        v.getNom(),
                        v.getValeur(),
                        v.getPrixSupplementaire(),
                        v.getStockSupplementaire(),
                        v.isActif()
                ))
                .collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product p) {
        return new ProductDto(
                p.getId(),
                p.getNom(),
                p.getDescription(),
                p.getPrix(),
                p.getStock(),
                p.getImageUrl(),
                p.isActif(),
                p.getSellerProfile().getId(),
                p.getSellerProfile().getUser().getNom() + " " + p.getSellerProfile().getUser().getPrenom(),
                convertCategoriesToDtos(p.getCategories()),
                convertVariantsToDtos(p.getVariants()),
                calculateAverageRating(p),
                p.getReviews() != null ? p.getReviews().size() : 0
        );
    }
}
