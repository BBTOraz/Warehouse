package bbt.tao.warehouse.repository;

import bbt.tao.warehouse.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    Optional<Product> findByBarcode(String barcode);

    List<Product> findByCategory_Id(Long categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.minStockLevel >= :minLevel")
    List<Product> findProductsWithLowStock(@Param("minLevel") Double minLevel);

    @Query("SELECT p FROM Product p WHERE p.isActive = true")
    List<Product> findAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
       "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
       "OR LOWER(p.barcode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Product> findProductsBySkuOrNameOrBarcode(@Param("searchTerm") String searchTerm);

    List<Product> findByIsActiveTrue();

    boolean existsBySku(String sku);
}
