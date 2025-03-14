package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.category.CategoryDTO;
import bbt.tao.warehouse.dto.category.CategorySummaryDTO;
import bbt.tao.warehouse.mapper.CategoryMapper;
import bbt.tao.warehouse.model.Category;
import bbt.tao.warehouse.repository.CategoryRepository;
import bbt.tao.warehouse.repository.ProductRepository;
import bbt.tao.warehouse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryDTO> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> findRootCategories() {
        List<Category> rootCategories = categoryRepository.findAllRootCategories();
        return rootCategories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> findSubcategories(Long parentId) {
        List<Category> subcategories = categoryRepository.findByParent_Id(parentId);
        return subcategories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoryDTO> findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO);
    }

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
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