package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.category.CategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CategoryService {

    List<CategoryDTO> findAllCategories();

    List<CategoryDTO> findRootCategories();

    List<CategoryDTO> findSubcategories(Long parentId);

    Optional<CategoryDTO> findCategoryById(Long id);

    CategoryDTO saveCategory(CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    boolean hasProducts(Long categoryId);
}