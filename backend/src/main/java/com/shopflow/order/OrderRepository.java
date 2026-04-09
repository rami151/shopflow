package com.shopflow.order;

import com.shopflow.shared.entity.Order;
import com.shopflow.shared.entity.Product;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByUserIdOrderByDateCommandeDesc(Long userId);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Optional<Order> findByNumeroCommande(String numeroCommande);

    Page<Order> findByStatut(OrderStatus statut, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o WHERE o.statut IN ('PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.statut IN ('PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED')")
    Long getTotalOrders();

    @Query("SELECT o FROM Order o WHERE o.statut IN ('PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED') ORDER BY o.dateCommande DESC")
    List<Order> findRecentDeliveredOrders(Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.statut = 'PENDING' ORDER BY o.dateCommande ASC")
    List<Order> findPendingOrders(Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.product.sellerProfile.user.email = :email AND o.statut = 'PENDING'")
    List<Order> findPendingOrdersBySellerEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN o.items oi WHERE oi.product.sellerProfile.user.email = :email AND o.statut IN ('PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED')")
    List<Order> findDeliveredOrdersBySellerEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o JOIN o.items oi WHERE oi.product.sellerProfile.user.email = :email AND o.statut IN ('PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal getRevenueBySellerEmail(@Param("email") String email);
}