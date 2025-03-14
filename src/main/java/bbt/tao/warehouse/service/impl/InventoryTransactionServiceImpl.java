package bbt.tao.warehouse.service.impl;

import bbt.tao.warehouse.dto.inventory.InventoryItemDTO;
import bbt.tao.warehouse.dto.inventory.InventoryTransactionDTO;
import bbt.tao.warehouse.exceptions.InsufficientStockException;
import bbt.tao.warehouse.mapper.InventoryItemMapper;
import bbt.tao.warehouse.mapper.InventoryTransactionMapper;
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
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionMapper transactionMapper;
    private final InventoryItemMapper inventoryItemMapper;

    @Autowired
    public InventoryTransactionServiceImpl(
            InventoryTransactionRepository transactionRepository,
            InventoryItemRepository inventoryItemRepository,
            InventoryTransactionMapper transactionMapper, InventoryItemMapper inventoryItemMapper) {
        this.transactionRepository = transactionRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.transactionMapper = transactionMapper;
        this.inventoryItemMapper = inventoryItemMapper;
    }

    @Override
    public List<InventoryTransactionDTO> findAllTransactions() {
        List<InventoryTransaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InventoryTransactionDTO> findTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDTO);
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByProduct(Long productId) {
        List<InventoryTransaction> transactions = transactionRepository.findByProduct_Id(productId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByType(TransactionType transactionType) {
        List<InventoryTransaction> transactions = transactionRepository.findByTransactionType(transactionType);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsBySupplier(Long supplierId) {
        List<InventoryTransaction> transactions = transactionRepository.findBySupplier_Id(supplierId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByCustomer(Long customerId) {
        List<InventoryTransaction> transactions = transactionRepository.findByCustomer_Id(customerId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByDocumentNumber(String documentNumber) {
        List<InventoryTransaction> transactions = transactionRepository.findByDocumentNumber(documentNumber);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<InventoryTransaction> transactions = transactionRepository.findByDateRange(startDate, endDate);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryTransactionDTO> findTransactionsByLocation(Long locationId) {
        List<InventoryTransaction> transactions = transactionRepository.findByLocation(locationId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryTransactionDTO createReceivingTransaction(InventoryTransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.RECEIVING);
        transaction.setTransactionDate(LocalDateTime.now());

        // Save transaction
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Update inventory
        updateInventoryOnReceiving(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public InventoryTransactionDTO createShippingTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.SHIPPING);
        transaction.setTransactionDate(LocalDateTime.now());

        // Check sufficient stock
        checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                transaction.getBatchNumber(), transaction.getQuantity());

        // Save transaction
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Update inventory
        updateInventoryOnShipping(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public InventoryTransactionDTO createTransferTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setTransactionDate(LocalDateTime.now());

        // Check sufficient stock
        checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                transaction.getBatchNumber(), transaction.getQuantity());

        // Save transaction
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Update inventory
        updateInventoryOnTransfer(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public InventoryTransactionDTO createAdjustmentTransaction(InventoryTransactionDTO transactionDTO) throws InsufficientStockException {
        if (transactionDTO == null) {
            throw new IllegalArgumentException("Transaction data cannot be null");
        }

        InventoryTransaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setTransactionType(TransactionType.ADJUSTMENT);
        transaction.setTransactionDate(LocalDateTime.now());

        // For negative adjustments, check sufficient stock
        if (transaction.getQuantity() < 0) {
            checkSufficientStock(transaction.getProduct().getId(), transaction.getSourceLocation().getId(),
                    transaction.getBatchNumber(), Math.abs(transaction.getQuantity()));
        }

        // Save transaction
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);

        // Update inventory
        updateInventoryOnAdjustment(transaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public List<InventoryItemDTO> getStockLevels(Long productId) {
        List<InventoryItem> items = inventoryItemRepository.findByProduct_Id(productId);
        return items.stream()
                .map(inventoryItemMapper::toDTO)
                .collect(Collectors.toList());
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

    private void checkSufficientStock(Long productId, Long locationId, String batchNumber, Double quantity) throws InsufficientStockException {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(productId, locationId, batchNumber);

        if (inventoryItemOptional.isEmpty() || inventoryItemOptional.get().getQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product ID: " + productId + " at location ID: " + locationId);
        }
    }

    private void updateInventoryOnReceiving(InventoryTransaction transaction) {
        Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository
                .findByProduct_IdAndLocation_IdAndBatchNumber(
                        transaction.getProduct().getId(),
                        transaction.getDestinationLocation().getId(),
                        transaction.getBatchNumber());

        InventoryItem inventoryItem;

        if (inventoryItemOptional.isPresent()) {
            // Update existing inventory
            inventoryItem = inventoryItemOptional.get();
            inventoryItem.setQuantity(inventoryItem.getQuantity() + transaction.getQuantity());
        } else {
            // Create new inventory record
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
