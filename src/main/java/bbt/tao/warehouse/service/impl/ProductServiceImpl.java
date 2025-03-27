package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.mapper.ProductMapper;
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
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductDTO> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return productMapper.toDTOList(products);
    }

    @Override
    public List<ProductDTO> findAllActiveProducts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        return productMapper.toDTOList(products);
    }

    @Override
    public Optional<ProductDTO> findProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDTO);
    }

    @Override
    public Optional<ProductDTO> findProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(productMapper::toDTO);
    }

    @Override
    public Optional<ProductDTO> findProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(productMapper::toDTO);
    }

    @Override
    public List<ProductDTO> findProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategory_Id(categoryId);
        return productMapper.toDTOList(products);
    }

    @Override
    public List<ProductDTO> findProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return productMapper.toDTOList(products);
    }

    @Override
    public List<ProductDTO> findProductsWithLowStock(Double minStock) {
        List<Product> products = productRepository.findProductsWithLowStock(minStock);
        return productMapper.toDTOList(products);
    }

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("Product data cannot be null");
        }

        Product product;

        if (productDTO.getId() != null) {
            // Update existing product
            Optional<Product> existingProduct = productRepository.findById(productDTO.getId());
            if (existingProduct.isPresent()) {
                product = existingProduct.get();
                productMapper.updateEntityFromDTO(productDTO, product);
                product.setUpdatedAt(LocalDateTime.now());
            } else {
                product = productMapper.toEntity(productDTO);
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
            }
        } else {
            // Create new product
            product = productMapper.toEntity(productDTO);
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());

            // Set default values if not provided
            if (product.getIsActive() == null) {
                product.setIsActive(true);
            }
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public List<ProductDTO> findProductsBySkuOrNameOrBarcode(String searchTerm) {
        return productMapper.toDTOList(productRepository.findProductsBySkuOrNameOrBarcode(searchTerm));
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    @Override
    public long countAllProducts() {
        return productRepository.count();
    }

    @Override
    public int getLowStockCount() {
        List<Product> lowStockProducts = productRepository.findProductsWithLowStock(1.0);
        return lowStockProducts.size();
    }
}