package com.shopflow.product;

import com.shopflow.shared.entity.Category;
import com.shopflow.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByActifTrue();
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNullAndActifTrue();
    }

    @Transactional(readOnly = true)
    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentIdAndActifTrue(parentId);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findByIdAndActifTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category.getParent() != null && category.getParent().getId() != null) {
            categoryRepository.findById(category.getParent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Catégorie parente non trouvée avec l'ID: " + category.getParent().getId()));
        }
        category.setActif(true);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category updatedData) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));

        if (updatedData.getNom() != null) {
            existing.setNom(updatedData.getNom());
        }
        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }
        if (updatedData.getParent() != null && updatedData.getParent().getId() != null) {
            categoryRepository.findById(updatedData.getParent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Catégorie parente non trouvée avec l'ID: " + updatedData.getParent().getId()));
            existing.setParent(updatedData.getParent());
        } else if (updatedData.getParent() != null) {
            existing.setParent(null);
        }

        return categoryRepository.save(existing);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'ID: " + id));
        softDeleteWithChildren(category);
    }

    private void softDeleteWithChildren(Category category) {
        category.setActif(false);
        categoryRepository.save(category);
        for (Category child : category.getChildren()) {
            if (child.isActif()) {
                softDeleteWithChildren(child);
            }
        }
    }
}
