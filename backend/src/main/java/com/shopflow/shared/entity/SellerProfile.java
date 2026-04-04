package com.shopflow.shared.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "seller_profiles")
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomBoutique;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String telephone;

    // average rating, updated externally
    @Column
    @Builder.Default
    private Double notesMoyenne = 0.0;

    // inactive profiles are hidden from the storefront
    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;

    // set once on first persist, never updated
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // owning side of the OneToOne with User
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "sellerProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @PrePersist
    protected void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }
}
