package com.shopflow.order;

import com.shopflow.shared.entity.Order;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}