package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.exceptions.InsufficientStockException;
import bbt.tao.warehouse.model.InventoryItem;
import bbt.tao.warehouse.model.InventoryTransaction;
import bbt.tao.warehouse.model.enums.TransactionType;
import bbt.tao.warehouse.repository.InventoryItemRepository;
import bbt.tao.warehouse.repository.InventoryTransactionRepository;
import bbt.tao.warehouse.service.InventoryTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryTransactionServiceImpl(InventoryTransactionRepository transactionRepository,
                                           InventoryItemRepository inventoryItemRepository) {
        this.transactionRepository = transactionRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Override
    public List<InventoryTransaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Optional<InventoryTransaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByProduct(Long productId) {
        return transactionRepository.findByProduct_Id(productId);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByType(TransactionType transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }

    @Override
    public List<InventoryTransaction> findTransactionsBySupplier(Long supplierId) {
        return transactionRepository.findBySupplier_Id(supplierId);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByCustomer(Long customerId) {
        return transactionRepository.findByCustomer_Id(customerId);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByDocumentNumber(String documentNumber) {
        return transactionRepository.findByDocumentNumber(documentNumber);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<InventoryTransaction> findTransactionsByLocation(Long locationId) {
        return transactionRepository.findByLocation(locationId);
    }

    @Override
    public InventoryTransaction createReceivingTransaction(InventoryTransaction transaction) {
        transaction.setTransactionType(TransactionType.RECEIVING);
        transaction.setTransactionDate(LocalDateTime.now());

        // Сохраняем транзакцию
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Обновляем остатки
        updateInventoryOnReceiving(transaction);

        return savedTransaction;
    }

    @Override
    public InventoryTransaction createShippingTransaction(InventoryTransaction transaction) throws InsufficientStockException {
        transaction.setTransactionType(TransactionType.SHIPPING);
        transaction.setTransactionDate(LocalDateTime.now());

        // Проверяем наличие достаточного количества товара
        checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                transaction.getBatchNumber(), transaction.getQuantity());

        // Сохраняем транзакцию
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Обновляем остатки
        updateInventoryOnShipping(transaction);

        return savedTransaction;
    }

    @Override
    public InventoryTransaction createTransferTransaction(InventoryTransaction transaction) throws InsufficientStockException {
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionDate(LocalDateTime.now());

        // Проверяем наличие достаточного количества товара
        checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                transaction.getBatchNumber(), transaction.getQuantity());

        // Сохраняем транзакцию
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Обновляем остатки
        updateInventoryOnTransfer(transaction);

        return savedTransaction;
    }

    @Override
    public InventoryTransaction createAdjustmentTransaction(InventoryTransaction transaction) throws InsufficientStockException {
        transaction.setTransactionType(TransactionType.ADJUSTMENT);
        transaction.setTransactionDate(LocalDateTime.now());

        // Если это отрицательная корректировка, проверяем наличие достаточного количества товара
        if (transaction.getQuantity() < 0) {
            checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                    transaction.getBatchNumber(), Math.abs(transaction.getQuantity()));
        }

        // Сохраняем транзакцию
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Обновляем остатки
        updateInventoryOnAdjustment(transaction);

        return savedTransaction;
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public List<InventoryItem> getStockLevels(Long productId) {
        return inventoryItemRepository.findByProduct_Id(productId);
    }

    private void updateInventoryOnTransfer(InventoryTransaction transaction) {
        // Decrease quantity at source location (similar to shipping)
        Optional<InventoryItem> sourceItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getSourceLocation().getId(),
                        transaction.getBatchNumber());

        if (sourceItemOptional.isPresent()) {
            InventoryItem sourceItem = sourceItemOptional.get();
            double newQuantity = sourceItem.getQuantity() - transaction.getQuantity();

            if (newQuantity > 0) {
                sourceItem.setQuantity(newQuantity);
                sourceItem.setLastInventoryDate(LocalDateTime.now());
                inventoryItemRepository.save(sourceItem);
            } else {
                inventoryItemRepository.delete(sourceItem);
            }
        }

        Optional<InventoryItem> destItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getDestinationLocation().getId(),
                        transaction.getBatchNumber());

        InventoryItem destItem;

        if (destItemOptional.isPresent()) {
            destItem = destItemOptional.get();
            destItem.setQuantity(destItem.getQuantity() + transaction.getQuantity());
        } else {
            destItem = new InventoryItem();
            destItem.setProduct(transaction.getProduct());
            destItem.setLocation(transaction.getDestinationLocation());
            destItem.setQuantity(transaction.getQuantity());
            destItem.setBatchNumber(transaction.getBatchNumber());
            destItem.setExpirationDate(transaction.getExpirationDate());
        }

        destItem.setLastInventoryDate(LocalDateTime.now());
        inventoryItemRepository.save(destItem);
    }

    private void updateInventoryOnAdjustment(InventoryTransaction transaction) {
        Optional<InventoryItem> itemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getSourceLocation().getId(),
                        transaction.getBatchNumber());

        InventoryItem item;

        if (itemOptional.isPresent()) {
            // Update existing inventory item
            item = itemOptional.get();
            double newQuantity = item.getQuantity() + transaction.getQuantity();

            if (newQuantity > 0) {
                item.setQuantity(newQuantity);
                item.setLastInventoryDate(LocalDateTime.now());
                inventoryItemRepository.save(item);
            } else {
                // Remove inventory record if quantity becomes zero or negative
                inventoryItemRepository.delete(item);
            }
        } else if (transaction.getQuantity() > 0) {
            // Create new inventory record for positive adjustment when no record exists
            item = new InventoryItem();
            item.setProduct(transaction.getProduct());
            item.setLocation(transaction.getSourceLocation());
            item.setQuantity(transaction.getQuantity());
            item.setBatchNumber(transaction.getBatchNumber());
            item.setExpirationDate(transaction.getExpirationDate());
            item.setLastInventoryDate(LocalDateTime.now());
            inventoryItemRepository.save(item);
        }
    }

    // Проверка наличия достаточного количества товара
    private void checkSufficientStock(Long productId, Long locationId, String batchNumber, Double quantity) throws InsufficientStockException {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(productId, locationId, batchNumber);

        if (!inventoryItemOptional.isPresent() || inventoryItemOptional.get().getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product ID: " + productId + " at location ID: " + locationId);
        }
    }

    // Обновление остатков при получении товара
    private void updateInventoryOnReceiving(InventoryTransaction transaction) {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getDestinationLocation().getId(),
                        transaction.getBatchNumber());

        InventoryItem inventoryItem;

        if (inventoryItemOptional.isPresent()) {
            // Обновляем существующий остаток
            inventoryItem = inventoryItemOptional.get();
            inventoryItem.setQuantity(inventoryItem.getQuantity() + transaction.getQuantity());
        } else {
            // Создаем новую запись об остатке
            inventoryItem = new InventoryItem();
            inventoryItem.setProduct(transaction.getProduct());
            inventoryItem.setLocation(transaction.getDestinationLocation());
            inventoryItem.setQuantity(transaction.getQuantity());
            inventoryItem.setBatchNumber(transaction.getBatchNumber());
            inventoryItem.setExpirationDate(transaction.getExpirationDate());
        }

        inventoryItem.setLastInventoryDate(LocalDateTime.now());
        inventoryItemRepository.save(inventoryItem);
    }

    // Обновление остатков при отгрузке товара
    private void updateInventoryOnShipping(InventoryTransaction transaction) {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getSourceLocation().getId(),
                        transaction.getBatchNumber());

        if (inventoryItemOptional.isPresent()) {
            InventoryItem inventoryItem = inventoryItemOptional.get();
            double newQuantity = inventoryItem.getQuantity() - transaction.getQuantity();

            if (newQuantity > 0) {
                // Обновляем существующий остаток
                inventoryItem.setQuantity(newQuantity);
                inventoryItem.setLastInventoryDate(LocalDateTime.now());
                inventoryItemRepository.save(inventoryItem);
            } else {
                // Если остаток равен 0, удаляем запись
                inventoryItemRepository.delete(inventoryItem);
            }
        }
    }
}
