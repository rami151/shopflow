package com.shopflow.dashboard;

import com.shopflow.auth.UserRepository;
import com.shopflow.dashboard.dto.*;
import com.shopflow.dashboard.dto.AdminDashboardResponse.*;
import com.shopflow.dashboard.dto.SellerDashboardResponse.*;
import com.shopflow.dashboard.dto.CustomerDashboardResponse.*;
import com.shopflow.order.OrderRepository;
import com.shopflow.product.ProductRepository;
import com.shopflow.review.ReviewRepository;
import com.shopflow.shared.entity.Order;
import com.shopflow.shared.entity.OrderItem;
import com.shopflow.shared.entity.Product;
import com.shopflow.shared.entity.Review;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.enums.OrderStatus;
import com.shopflow.shared.enums.Role;
import com.shopflow.shared.exception.ResourceNotFoundException;
import com.shopflow.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Object getDashboard(String userEmail) {
        User user = getUserByEmail(userEmail);

        return switch (user.getRole()) {
            case ADMIN -> getAdminDashboard();
            case SELLER -> getSellerDashboard(userEmail);
            case CUSTOMER -> getCustomerDashboard(user.getId());
            default -> throw new UnauthorizedException("Rôle non reconnu");
        };
    }

    private AdminDashboardResponse getAdminDashboard() {
        BigDecimal chiffreAffairesGlobal = orderRepository.getTotalRevenue();
        Long totalCommandes = orderRepository.getTotalOrders();
        long totalProduits = productRepository.count();
        long totalVendeurs = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SELLER)
                .count();
        long totalClients = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.CUSTOMER)
                .count();

        List<Order> recentOrders = orderRepository.findRecentDeliveredOrders(PageRequest.of(0, 10));
        List<AdminDashboardResponse.OrderSummary> commandesRecentes = recentOrders.stream()
                .map(this::toAdminOrderSummary)
                .collect(Collectors.toList());

        Map<Long, Integer> productSales = new HashMap<>();
        Map<Long, BigDecimal> productRevenue = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            if (order.getStatut() != OrderStatus.CANCELLED) {
                for (OrderItem item : order.getItems()) {
                    Long productId = item.getProduct().getId();
                    productSales.merge(productId, item.getQuantite(), Integer::sum);
                    productRevenue.merge(productId, 
                        item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())), 
                        BigDecimal::add);
                }
            }
        }

        List<ProductStat> topProduits = productSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Product p = productRepository.findById(e.getKey()).orElse(null);
                    return p != null ? new ProductStat(p.getId(), p.getNom(), e.getValue(), productRevenue.get(e.getKey())) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, List<Order>> ordersBySeller = new HashMap<>();
        for (Order order : orderRepository.findAll()) {
            if (order.getStatut() != OrderStatus.CANCELLED) {
                for (OrderItem item : order.getItems()) {
                    Long sellerId = item.getProduct().getSellerProfile().getId();
                    ordersBySeller.computeIfAbsent(sellerId, k -> new ArrayList<>()).add(order);
                }
            }
        }

        List<SellerStat> topVendeurs = ordersBySeller.entrySet().stream()
                .map(e -> {
                    BigDecimal revenue = e.getValue().stream()
                            .flatMap(o -> o.getItems().stream())
                            .filter(i -> i.getProduct().getSellerProfile().getId().equals(e.getKey()))
                            .map(i -> i.getPrixUnitaire().multiply(BigDecimal.valueOf(i.getQuantite())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    User seller = e.getValue().stream()
                            .flatMap(o -> o.getItems().stream())
                            .filter(i -> i.getProduct().getSellerProfile().getId().equals(e.getKey()))
                            .map(i -> i.getProduct().getSellerProfile().getUser())
                            .findFirst()
                            .orElse(null);
                    return seller != null ? new SellerStat(e.getKey(), seller.getNom() + " " + seller.getPrenom(), revenue, e.getValue().size()) : null;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(SellerStat::revenu).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return new AdminDashboardResponse(
                chiffreAffairesGlobal,
                totalCommandes.intValue(),
                (int) totalProduits,
                (int) totalVendeurs,
                (int) totalClients,
                topProduits,
                topVendeurs,
                commandesRecentes
        );
    }

    private SellerDashboardResponse getSellerDashboard(String email) {
        BigDecimal revenuTotal = orderRepository.getRevenueBySellerEmail(email);

        List<Order> pendingOrders = orderRepository.findPendingOrdersBySellerEmail(email, PageRequest.of(0, 50));
        int commandesEnAttente = (int) pendingOrders.stream()
                .flatMap(o -> o.getItems().stream())
                .filter(i -> i.getProduct().getSellerProfile().getUser().getEmail().equals(email))
                .count();

        List<Order> deliveredOrders = orderRepository.findDeliveredOrdersBySellerEmail(email, PageRequest.of(0, 100));
        Set<Long> uniqueOrderIds = deliveredOrders.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());
        int commandesLivrees = uniqueOrderIds.size();

        List<Product> sellerProducts = productRepository.findAll().stream()
                .filter(p -> p.getSellerProfile() != null && p.getSellerProfile().getUser().getEmail().equals(email))
                .filter(Product::isActif)
                .collect(Collectors.toList());

        List<ProductAlert> alertesStock = new ArrayList<>();
        for (Product p : sellerProducts) {
            String niveau = "NORMAL";
            if (p.getStock() <= 0) {
                niveau = "RUPTURE";
            } else if (p.getStock() <= 5) {
                niveau = "CRITIQUE";
            } else if (p.getStock() <= 10) {
                niveau = "FAIBLE";
            }

            if (!niveau.equals("NORMAL")) {
                alertesStock.add(new ProductAlert(p.getId(), p.getNom(), p.getStock(), niveau));
            }

            for (com.shopflow.shared.entity.ProductVariant v : p.getVariants()) {
                if (v.isActif()) {
                    int totalStock = p.getStock() + v.getStockSupplementaire();
                    String variantNiveau = "NORMAL";
                    if (totalStock <= 0) {
                        variantNiveau = "RUPTURE";
                    } else if (totalStock <= 5) {
                        variantNiveau = "CRITIQUE";
                    } else if (totalStock <= 10) {
                        variantNiveau = "FAIBLE";
                    }

                    if (!variantNiveau.equals("NORMAL")) {
                        alertesStock.add(new ProductAlert(v.getId(), p.getNom() + " (" + v.getNom() + " " + v.getValeur() + ")", totalStock, variantNiveau));
                    }
                }
            }
        }

        List<Order> recentOrders = orderRepository.findPendingOrdersBySellerEmail(email, PageRequest.of(0, 10));
        List<SellerDashboardResponse.OrderSummary> commandesRecentes = recentOrders.stream()
                .map(this::toSellerOrderSummary)
                .collect(Collectors.toList());

        return new SellerDashboardResponse(
                revenuTotal,
                commandesEnAttente,
                commandesLivrees,
                alertesStock,
                commandesRecentes
        );
    }

    private CustomerDashboardResponse getCustomerDashboard(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByDateCommandeDesc(userId);

        List<CustomerDashboardResponse.OrderSummary> commandesEnCours = orders.stream()
                .filter(o -> o.getStatut() != OrderStatus.CANCELLED && o.getStatut() != OrderStatus.DELIVERED)
                .map(this::toCustomerOrderSummary)
                .limit(10)
                .collect(Collectors.toList());

        List<Review> reviews = reviewRepository.findByUserIdOrderByDateCreationDesc(userId);
        List<CustomerDashboardResponse.ReviewSummary> derniersAvis = reviews.stream()
                .filter(Review::isApprouve)
                .limit(10)
                .map(this::toReviewSummary)
                .collect(Collectors.toList());

        return new CustomerDashboardResponse(commandesEnCours, derniersAvis);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
    }

    private AdminDashboardResponse.OrderSummary toAdminOrderSummary(Order order) {
        String clientNom = order.getUser().getNom() + " " + order.getUser().getPrenom();
        return new AdminDashboardResponse.OrderSummary(
                order.getId(),
                order.getNumeroCommande(),
                clientNom,
                order.getTotalTTC(),
                order.getStatut().name(),
                order.getDateCommande()
        );
    }

    private SellerDashboardResponse.OrderSummary toSellerOrderSummary(Order order) {
        return new SellerDashboardResponse.OrderSummary(
                order.getId(),
                order.getNumeroCommande(),
                order.getTotalTTC(),
                order.getStatut().name(),
                order.getDateCommande()
        );
    }

    private CustomerDashboardResponse.OrderSummary toCustomerOrderSummary(Order order) {
        return new CustomerDashboardResponse.OrderSummary(
                order.getId(),
                order.getNumeroCommande(),
                order.getTotalTTC(),
                order.getStatut().name(),
                order.getDateCommande()
        );
    }

    private ReviewSummary toReviewSummary(Review review) {
        return new ReviewSummary(
                review.getId(),
                review.getProduct().getId(),
                review.getProduct().getNom(),
                review.getProduct().getImageUrl(),
                review.getNote(),
                review.getCommentaire(),
                review.getDateCreation()
        );
    }
}