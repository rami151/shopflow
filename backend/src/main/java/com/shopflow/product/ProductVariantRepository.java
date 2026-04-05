package com.shopflow.product;

import com.shopflow.shared.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // All variants for a specific product
    List<ProductVariant> findByProductId(Long productId);

    // Active variants for a specific product
    List<ProductVariant> findByProductIdAndActifTrue(Long productId);

    // Active variant by ID
    Optional<ProductVariant> findByIdAndActifTrue(Long id);

    // Variant by product and attribute name/value (e.g. "Couleur"/"Rouge")
    Optional<ProductVariant> findByProductIdAndNomAndValeur(Long productId, String nom, String valeur);
}
