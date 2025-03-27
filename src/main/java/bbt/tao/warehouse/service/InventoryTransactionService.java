package bbt.tao.warehouse.service;

    import bbt.tao.warehouse.dto.inventory.InventoryItemDTO;
    import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
    import bbt.tao.warehouse.dto.inventory.InventoryTransactionSummaryDTO;
    import bbt.tao.warehouse.exceptions.InsufficientStockException;
    import bbt.tao.warehouse.model.InventoryItem;
    import bbt.tao.warehouse.model.enums.TransactionType;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;

    @Service
    public interface InventoryTransactionService {

        List<InventoryTransactionDTO> findAllTransactions();

        Optional<InventoryTransactionDTO> findTransactionById(Long id);

        List<InventoryTransactionDTO> findTransactionsByProduct(Long productId);

        List<InventoryTransactionDTO> findTransactionsByType(TransactionType transactionType);

        List<InventoryTransactionDTO> findTransactionsBySupplier(Long supplierId);

        List<InventoryTransactionDTO> findTransactionsByCustomer(Long customerId);

        List<InventoryTransactionDTO> findTransactionsByDocumentNumber(String documentNumber);

        List<InventoryTransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

        List<InventoryTransactionDTO> findTransactionsByLocation(Long locationId);

        InventoryTransactionDTO createReceivingTransaction(InventoryTransactionDTO transactionDTO);

        InventoryTransactionDTO createShippingTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException;

        InventoryTransactionDTO createTransferTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException;

        InventoryTransactionDTO createAdjustmentTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException;

        void deleteTransaction(Long id);

        List<InventoryItemDTO> getStockLevels(Long productId);

        List<InventoryTransactionDTO> findTransactionsByUser(Long userId);

        InventoryTransactionDTO updateTransaction(Long id, InventoryTransactionDTO transactionDTO) throws InsufficientStockException;
    }