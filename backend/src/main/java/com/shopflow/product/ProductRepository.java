package com.shopflow.product;

import com.shopflow.shared.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByActifTrue(Pageable pageable);

    Optional<Product> findByIdAndActifTrue(Long id);

    Page<Product> findByActifTrueAndSellerProfileId(Long sellerId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.actif = true AND (p.nom LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE p.actif = true AND c.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.actif = true AND p.prix BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN OrderItem oi ON oi.product.id = p.id JOIN oi.order o WHERE o.statut = 'DELIVERED' GROUP BY p.id ORDER BY COUNT(oi.id) DESC")
    Page<Product> findTopSelling(Pageable pageable);
}
