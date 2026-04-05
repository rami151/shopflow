package com.shopflow.product;

import com.shopflow.auth.UserRepository;
import com.shopflow.shared.entity.Category;
import com.shopflow.shared.entity.Product;
import com.shopflow.shared.entity.ProductVariant;
import com.shopflow.shared.entity.SellerProfile;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.exception.ResourceNotFoundException;
import com.shopflow.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByActifTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getProductsWithFilters(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Long sellerId, Pageable pageable) {
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable);
        }
        if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
        }
        if (sellerId != null) {
            return productRepository.findByActifTrueAndSellerProfileId(sellerId, pageable);
        }
        return productRepository.findByActifTrue(pageable);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id));
    }

    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Product> getTopSellingProducts(Pageable pageable) {
        return productRepository.findTopSelling(pageable);
    }

    @Transactional
    public Product createProduct(Product product, Set<Long> categoryIds, String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + authenticatedEmail));

        SellerProfile sellerProfile = user.getSellerProfile();
        if (sellerProfile == null) {
            throw new UnauthorizedException("Utilisateur non autorisé à créer des produits");
        }

        product.setSellerProfile(sellerProfile);
        product.setActif(true);

        if (categoryIds != null && !categoryIds.isEmpty()) {
            List<Category> categories = categoryIds.stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id)))
                    .collect(Collectors.toList());
            product.setCategories(categories);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedData, Set<Long> categoryIds, String authenticatedEmail) {
        Product existing = productRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id));

        if (!existing.getSellerProfile().getUser().getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        if (updatedData.getNom() != null) {
            existing.setNom(updatedData.getNom());
        }
        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }
        if (updatedData.getPrix() != null) {
            existing.setPrix(updatedData.getPrix());
        }
        if (updatedData.getStock() != null) {
            existing.setStock(updatedData.getStock());
        }
        if (updatedData.getImageUrl() != null) {
            existing.setImageUrl(updatedData.getImageUrl());
        }

        if (categoryIds != null) {
            List<Category> categories = categoryIds.stream()
                    .map(catId -> categoryRepository.findById(catId)
                            .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + catId)))
                    .collect(Collectors.toList());
            existing.setCategories(categories);
        }

        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Long id, String authenticatedEmail) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id));

        if (!product.getSellerProfile().getUser().getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer ce produit");
        }

        product.setActif(false);
        for (ProductVariant variant : product.getVariants()) {
            variant.setActif(false);
        }
        productRepository.save(product);
    }

    @Transactional
    public ProductVariant addVariant(Long productId, ProductVariant variant, String authenticatedEmail) {
        Product product = productRepository.findByIdAndActifTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + productId));

        if (!product.getSellerProfile().getUser().getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier ce produit");
        }

        variant.setProduct(product);
        variant.setActif(true);
        return productVariantRepository.save(variant);
    }

    @Transactional
    public void deleteVariant(Long variantId, String authenticatedEmail) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variante de produit non trouvée avec l'ID: " + variantId));

        if (!variant.getProduct().getSellerProfile().getUser().getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette variante");
        }

        variant.setActif(false);
        productVariantRepository.save(variant);
    }
}
