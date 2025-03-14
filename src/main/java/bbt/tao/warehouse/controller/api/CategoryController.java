package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.category.CategoryDTO;
import bbt.tao.warehouse.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return categoryService.findCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public CategoryDTO createCategory(@RequestBody CategoryDTO category) {
        return categoryService.saveCategory(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public CategoryDTO updateCategory(@PathVariable Long id, @RequestBody CategoryDTO category) {
        category.setId(id);
        return categoryService.saveCategory(category);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (categoryService.hasProducts(id)) {
            return ResponseEntity.badRequest().build();
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}