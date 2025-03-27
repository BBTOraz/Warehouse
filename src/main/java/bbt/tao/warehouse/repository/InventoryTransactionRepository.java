package bbt.tao.warehouse.repository;


import bbt.tao.warehouse.model.InventoryTransaction;
import bbt.tao.warehouse.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProduct_Id(Long productId);

    List<InventoryTransaction> findByTransactionType(TransactionType transactionType);

    List<InventoryTransaction> findBySupplier_Id(Long supplierId);

    List<InventoryTransaction> findByCustomer_Id(Long customerId);

    List<InventoryTransaction> findByDocumentNumber(String documentNumber);

    @Query("SELECT t FROM InventoryTransaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    List<InventoryTransaction> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t FROM InventoryTransaction t WHERE t.sourceLocation.id = :locationId OR t.destinationLocation.id = :locationId")
    List<InventoryTransaction> findByLocation(@Param("locationId") Long locationId);

    List<InventoryTransaction> findByUser_Id(Long userId);
}