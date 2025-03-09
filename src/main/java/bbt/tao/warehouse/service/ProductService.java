package bbt.tao.warehouse.service;

import bbt.tao.warehouse.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {

    List<Product> findAllProducts();

    List<Product> findAllActiveProducts();

    Optional<Product> findProductById(Long id);

    Optional<Product> findProductBySku(String sku);

    Optional<Product> findProductByBarcode(String barcode);

    List<Product> findProductsByCategory(Long categoryId);

    List<Product> findProductsByName(String name);

    List<Product> findProductsWithLowStock();

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    boolean existsBySku(String sku);
}