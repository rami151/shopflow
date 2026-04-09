package com.shopflow.review;

import com.shopflow.shared.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserIdOrderByDateCreationDesc(Long userId);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByApprouveTrue(Pageable pageable);

    List<Review> findByProductIdAndApprouveTrue(Long productId);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductIdAndApprouveFalse(Long userId, Long productId);

    @Query("SELECT COALESCE(AVG(r.note), 0.0) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    Long countApprovedReviewsByProductId(@Param("productId") Long productId);

    Page<Review> findByApprouveFalse(Pageable pageable);
}