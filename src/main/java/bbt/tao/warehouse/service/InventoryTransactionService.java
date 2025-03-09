package bbt.tao.warehouse.service;

import bbt.tao.warehouse.exceptions.InsufficientStockException;
import bbt.tao.warehouse.model.InventoryItem;
import bbt.tao.warehouse.model.InventoryTransaction;
import bbt.tao.warehouse.model.enums.TransactionType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public interface InventoryTransactionService {

    List<InventoryTransaction> findAllTransactions();

    Optional<InventoryTransaction> findTransactionById(Long id);

    List<InventoryTransaction> findTransactionsByProduct(Long productId);

    List<InventoryTransaction> findTransactionsByType(TransactionType transactionType);

    List<InventoryTransaction> findTransactionsBySupplier(Long supplierId);

    List<InventoryTransaction> findTransactionsByCustomer(Long customerId);

    List<InventoryTransaction> findTransactionsByDocumentNumber(String documentNumber);

    List<InventoryTransaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<InventoryTransaction> findTransactionsByLocation(Long locationId);

    InventoryTransaction createReceivingTransaction(InventoryTransaction transaction);

    InventoryTransaction createShippingTransaction(InventoryTransaction transaction) throws InsufficientStockException;

    InventoryTransaction createTransferTransaction(InventoryTransaction transaction) throws InsufficientStockException;

    InventoryTransaction createAdjustmentTransaction(InventoryTransaction transaction) throws InsufficientStockException;

    void deleteTransaction(Long id);

    List<InventoryItem> getStockLevels(Long productId);
}

