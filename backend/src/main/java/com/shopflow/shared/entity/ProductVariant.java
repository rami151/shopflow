package com.shopflow.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // variant attribute name, e.g. "Taille", "Couleur", "Stockage"
    @Column(nullable = false)
    private String nom;

    // variant attribute value, e.g. "L", "Rouge", "256GB"
    @Column(nullable = false)
    private String valeur;

    // extra cost on top of the base product price (can be 0.00)
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal prixSupplementaire = BigDecimal.ZERO;

    // additional stock specific to this variant
    @Column(nullable = false)
    @Builder.Default
    private Integer stockSupplementaire = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
