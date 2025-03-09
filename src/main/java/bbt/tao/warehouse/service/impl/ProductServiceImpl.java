package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.model.Product;
import bbt.tao.warehouse.repository.ProductRepository;
import bbt.tao.warehouse.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> findAllActiveProducts() {
        return productRepository.findAllActiveProducts();
    }

    @Override
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> findProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    @Override
    public Optional<Product> findProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    @Override
    public List<Product> findProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_Id(categoryId);
    }

    @Override
    public List<Product> findProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> findProductsWithLowStock() {
        return productRepository.findProductsWithLowStock(0.0);
    }

    @Override
    public Product saveProduct(Product product) {
        LocalDateTime now = LocalDateTime.now();
        if (product.getId() == null) {
            product.setCreatedAt(now);
        }
        product.setUpdatedAt(now);
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.findBySku(sku).isPresent();
    }
}
