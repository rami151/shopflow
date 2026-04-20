package com.shopflow.product;

import com.shopflow.shared.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
}
