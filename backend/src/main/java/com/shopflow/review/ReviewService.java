package com.shopflow.review;

import com.shopflow.auth.UserRepository;
import com.shopflow.order.OrderRepository;
import com.shopflow.product.ProductRepository;
import com.shopflow.review.dto.CreateReviewRequest;
import com.shopflow.review.dto.ReviewResponse;
import com.shopflow.shared.entity.Order;
import com.shopflow.shared.entity.Product;
import com.shopflow.shared.entity.Review;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.enums.OrderStatus;
import com.shopflow.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewResponse createReview(String userEmail, CreateReviewRequest request) {
        User user = getUserByEmail(userEmail);
        
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID: " + request.productId()));

        boolean hasPurchased = orderRepository.findAll().stream()
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .filter(o -> o.getStatut() == OrderStatus.DELIVERED || o.getStatut() == OrderStatus.SHIPPED)
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));

        if (!hasPurchased) {
            throw new IllegalArgumentException("Vous ne pouvez laisser un avis que sur un produit acheté");
        }

        if (reviewRepository.findByUserIdAndProductId(user.getId(), product.getId()).isPresent()) {
            throw new IllegalArgumentException("Vous avez déjà laissé un avis sur ce produit");
        }

        Review review = Review.builder()
                .user(user)
                .product(product)
                .note(request.note())
                .commentaire(request.commentaire())
                .approuve(false)
                .build();

        review = reviewRepository.save(review);
        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndApprouveTrue(productId);
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviewsPaged(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        return reviews.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Double getProductAverageRating(Long productId) {
        return reviewRepository.getAverageRatingByProductId(productId);
    }

    @Transactional(readOnly = true)
    public Long getProductReviewCount(Long productId) {
        return reviewRepository.countApprovedReviewsByProductId(productId);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getPendingReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByApprouveFalse(pageable);
        return reviews.map(this::toResponse);
    }

    @Transactional
    public ReviewResponse approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé avec l'ID: " + reviewId));
        
        review.setApprouve(true);
        review = reviewRepository.save(review);
        return toResponse(review);
    }

    @Transactional
    public ReviewResponse rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé avec l'ID: " + reviewId));
        
        reviewRepository.delete(review);
        return null;
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        User user = getUserByEmail(userEmail);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé avec l'ID: " + reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas supprimer cet avis");
        }

        reviewRepository.delete(review);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private ReviewResponse toResponse(Review review) {
        String userNom = review.getUser().getNom() + " " + review.getUser().getPrenom();
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getProduct().getNom(),
                userNom,
                review.getNote(),
                review.getCommentaire(),
                review.isApprouve(),
                review.getDateCreation()
        );
    }
}