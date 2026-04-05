package com.shopflow.product;

import com.shopflow.product.dto.CategoryDto;
import com.shopflow.product.dto.CreateCategoryRequest;
import com.shopflow.shared.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "API de gestion des catégories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Récupérer toutes les catégories actives", description = "Endpoint public - retourne toutes les catégories actives")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categoryDtos = categoryService.getAllActiveCategories().stream()
                .map(c -> new CategoryDto(
                        c.getId(),
                        c.getNom(),
                        c.getDescription(),
                        c.getParent() != null ? c.getParent().getId() : null,
                        c.getParent() != null ? c.getParent().getNom() : null,
                        c.isActif()
                ))
                .toList();
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/root")
    @Operation(summary = "Récupérer les catégories racines", description = "Endpoint public - retourne uniquement les catégories de premier niveau")
    public ResponseEntity<List<CategoryDto>> getRootCategories() {
        List<CategoryDto> categoryDtos = categoryService.getRootCategories().stream()
                .map(c -> new CategoryDto(
                        c.getId(),
                        c.getNom(),
                        c.getDescription(),
                        c.getParent() != null ? c.getParent().getId() : null,
                        c.getParent() != null ? c.getParent().getNom() : null,
                        c.isActif()
                ))
                .toList();
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Récupérer les sous-catégories", description = "Endpoint public - retourne les sous-catégories d'une catégorie parent")
    public ResponseEntity<List<CategoryDto>> getSubcategories(@PathVariable Long id) {
        List<CategoryDto> categoryDtos = categoryService.getSubcategories(id).stream()
                .map(c -> new CategoryDto(
                        c.getId(),
                        c.getNom(),
                        c.getDescription(),
                        c.getParent() != null ? c.getParent().getId() : null,
                        c.getParent() != null ? c.getParent().getNom() : null,
                        c.isActif()
                ))
                .toList();
        return ResponseEntity.ok(categoryDtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une catégorie par ID", description = "Endpoint public - retourne les détails d'une catégorie")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        CategoryDto categoryDto = new CategoryDto(
                category.getId(),
                category.getNom(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getParent() != null ? category.getParent().getNom() : null,
                category.isActif()
        );
        return ResponseEntity.ok(categoryDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une nouvelle catégorie", description = "Réservé ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        Category category = new Category();
        category.setNom(request.nom());
        category.setDescription(request.description());
        if (request.parentId() != null) {
            Category parent = categoryService.getCategoryById(request.parentId());
            category.setParent(parent);
        }
        Category saved = categoryService.createCategory(category);
        CategoryDto categoryDto = new CategoryDto(
                saved.getId(),
                saved.getNom(),
                saved.getDescription(),
                saved.getParent() != null ? saved.getParent().getId() : null,
                saved.getParent() != null ? saved.getParent().getNom() : null,
                saved.isActif()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour une catégorie", description = "Réservé ADMIN", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CreateCategoryRequest request) {
        Category category = new Category();
        category.setNom(request.nom());
        category.setDescription(request.description());
        if (request.parentId() != null) {
            Category parent = categoryService.getCategoryById(request.parentId());
            category.setParent(parent);
        }
        Category updated = categoryService.updateCategory(id, category);
        CategoryDto categoryDto = new CategoryDto(
                updated.getId(),
                updated.getNom(),
                updated.getDescription(),
                updated.getParent() != null ? updated.getParent().getId() : null,
                updated.getParent() != null ? updated.getParent().getNom() : null,
                updated.isActif()
        );
        return ResponseEntity.ok(categoryDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une catégorie (soft delete)", description = "Réservé ADMIN - suppression logique uniquement", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
