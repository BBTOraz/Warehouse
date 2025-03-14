package bbt.tao.warehouse.service;

import bbt.tao.warehouse.dto.product.ProductDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {

    List<ProductDTO> findAllProducts();

    List<ProductDTO> findAllActiveProducts();

    Optional<ProductDTO> findProductById(Long id);

    Optional<ProductDTO> findProductBySku(String sku);

    Optional<ProductDTO> findProductByBarcode(String barcode);

    List<ProductDTO> findProductsByCategory(Long categoryId);

    List<ProductDTO> findProductsByName(String name);

    List<ProductDTO> findProductsWithLowStock(Double minStock);

    ProductDTO saveProduct(ProductDTO productDTO);

    void deleteProduct(Long id);

    boolean existsBySku(String sku);
}