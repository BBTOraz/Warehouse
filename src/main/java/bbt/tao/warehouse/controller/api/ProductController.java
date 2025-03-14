package bbt.tao.warehouse.controller.api;

import bbt.tao.warehouse.dto.product.ProductDTO;
import bbt.tao.warehouse.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ProductDTO> getAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public List<ProductDTO> getActiveProducts() {
        return productService.findAllActiveProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with id: " + id));
    }

    @GetMapping("/sku/{sku}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        return productService.findProductBySku(sku)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with SKU: " + sku));
    }

    @GetMapping("/barcode/{barcode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductDTO> getProductByBarcode(@PathVariable String barcode) {
        return productService.findProductByBarcode(barcode)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found with barcode: " + barcode));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public List<ProductDTO> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.findProductsByCategory(categoryId);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<ProductDTO> searchProducts(@RequestParam String name) {
        return productService.findProductsByName(name);
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<ProductDTO> getLowStockProducts(@RequestParam Double minStock) {
        return productService.findProductsWithLowStock(minStock);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        if (productDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product data cannot be empty");
        }
        if (productDTO.getSku() != null && productService.existsBySku(productDTO.getSku())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Product with SKU " + productDTO.getSku() + " already exists");
        }

        ProductDTO createdProduct = productService.saveProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO) {

        if (productDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Product data cannot be empty");
        }

        productService.findProductById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot update. Product not found with id: " + id));

        if (productDTO.getSku() != null && productService.existsBySku(productDTO.getSku())) {
            productService.findProductBySku(productDTO.getSku())
                    .ifPresent(existingProduct -> {
                        if (!existingProduct.getId().equals(id)) {
                            throw new ResponseStatusException(
                                    HttpStatus.CONFLICT, "Another product with SKU " + productDTO.getSku() + " already exists");
                        }
                    });
        }

        productDTO.setId(id);
        ProductDTO updatedProduct = productService.saveProduct(productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.findProductById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cannot delete. Product not found with id: " + id));

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}