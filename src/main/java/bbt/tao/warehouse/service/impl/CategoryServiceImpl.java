package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.model.Category;
import bbt.tao.warehouse.repository.CategoryRepository;
import bbt.tao.warehouse.repository.ProductRepository;
import bbt.tao.warehouse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findRootCategories() {
        return categoryRepository.findAllRootCategories();
    }

    @Override
    public List<Category> findSubcategories(Long parentId) {
        return categoryRepository.findByParent_Id(parentId);
    }

    @Override
    public Optional<Category> findCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean hasProducts(Long categoryId) {
        return !productRepository.findByCategory_Id(categoryId).isEmpty();
    }
}
