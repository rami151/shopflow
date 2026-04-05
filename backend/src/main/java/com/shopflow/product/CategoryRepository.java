package com.shopflow.product;

import com.shopflow.shared.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByActifTrue();

    Optional<Category> findByIdAndActifTrue(Long id);

    List<Category> findByParentIsNullAndActifTrue();

    List<Category> findByParentIdAndActifTrue(Long parentId);
}
