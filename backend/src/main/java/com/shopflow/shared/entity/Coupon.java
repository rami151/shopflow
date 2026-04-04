package com.shopflow.shared.entity;

import com.shopflow.shared.enums.CouponType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;
    @Column(nullable = false)
    private Integer usagesMax;
    @Column(nullable = false)
    @Builder.Default
    private Integer usagesActuels = 0;
    @Column(nullable = false)
    private LocalDate dateExpiration;
    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;
}
