package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CategoryService {

    List<Category> findAllCategories();

    List<Category> findRootCategories();

    List<Category> findSubcategories(Long parentId);

    Optional<Category> findCategoryById(Long id);

    Category saveCategory(Category category);

    void deleteCategory(Long id);

    boolean hasProducts(Long categoryId);
}
